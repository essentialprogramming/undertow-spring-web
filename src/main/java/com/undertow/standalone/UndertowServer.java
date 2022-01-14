package com.undertow.standalone;


import com.util.jsp.TldLocator;
import com.server.Server;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.jsp.HackInstanceManager;
import io.undertow.jsp.JspServletBuilder;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.GracefulShutdownHandler;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.StuckThreadDetectionHandler;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.server.handlers.resource.ResourceHandler;
import io.undertow.servlet.api.*;
import io.undertow.servlet.util.ImmediateInstanceFactory;
import org.apache.jasper.deploy.JspPropertyGroup;
import org.apache.jasper.deploy.TagLibraryInfo;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletException;

import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.config.ApplicationContextFactory.getSpringApplicationContext;
import static com.util.cloud.Environment.getProperty;
import static io.undertow.servlet.Servlets.*;


public class UndertowServer {

    public final Lock LOCK = new ReentrantLock();

    private volatile Undertow server;
    private final String host;
    private final int port;
    private final String deploymentName;

    public UndertowServer(String host, int port, String deploymentName) {
        this.host = host;
        this.port = port;
        this.deploymentName = deploymentName;
    }


    private static ServletInfo createDispatcherServlet(WebApplicationContext context) {
        InstanceFactory<DispatcherServlet> factory = new ImmediateInstanceFactory<>(new DispatcherServlet(context));
        return servlet("DispatcherServlet", DispatcherServlet.class, factory)
                .addMapping("/")
                .setLoadOnStartup(1)
                .setAsyncSupported(true);
    }

    private static ListenerInfo createContextLoaderListener(WebApplicationContext context) {
        InstanceFactory<ContextLoaderListener> factory = new ImmediateInstanceFactory<>(new ContextLoaderListener(context));
        return new ListenerInfo(ContextLoaderListener.class, factory);
    }


    private HttpHandler bootstrap() throws ServletException {
        final DeploymentInfo servletBuilder = deployment()
                .setClassLoader(Server.class.getClassLoader())
                .setContextPath("/")
                .addListeners(createContextLoaderListener(getSpringApplicationContext()))
                .setResourceManager(new ClassPathResourceManager(Server.class.getClassLoader(), "webapp/resources"))
                .addWelcomePage("index.html")
                .setDeploymentName(deploymentName)
                .addServlet(createDispatcherServlet(getSpringApplicationContext()))
                .addServlet( JspServletBuilder.createServlet("jspServlet", "*.jsp"));

        // configure jsp servlet
        HashMap<String, TagLibraryInfo> tagLibs = TldLocator.createTldInfos();
        JspServletBuilder.setupDeployment(servletBuilder, new HashMap<String, JspPropertyGroup>(), tagLibs, new HackInstanceManager());


        final DeploymentManager manager = defaultContainer().addDeployment(servletBuilder);
        manager.deploy();

        //Servlet handler
        final HttpHandler servletHandler = manager.start();

        //Open API resource handler
        final ResourceHandler resourceHandler = new ResourceHandler(new ClassPathResourceManager(Server.class.getClassLoader(), "apidoc"))
                .addWelcomeFiles("index.html")
                .setDirectoryListingEnabled(false);



        final PathHandler pathHandler = Handlers.path()
                .addPrefixPath("/", servletHandler)
                .addPrefixPath("apidoc", resourceHandler);

        return pathHandler;
    }

    public void start() throws ServletException {

        final HttpHandler httpHandler = bootstrap();
        final StuckThreadDetectionHandler stuck = new StuckThreadDetectionHandler(getProperty("thread.execution.time", 100), httpHandler);
        final GracefulShutdownHandler shutdown = Handlers.gracefulShutdown(stuck);

        LOCK.lock();

        server = Undertow.builder()
                .addHttpListener(port, host)
                .setHandler(shutdown)
                .setServerOption(UndertowOptions.ENABLE_HTTP2, true)
                .build();

        server.start();

        LOCK.unlock();
    }


    public void stop() {
        server.stop();
        LOCK.unlock();
    }

}
