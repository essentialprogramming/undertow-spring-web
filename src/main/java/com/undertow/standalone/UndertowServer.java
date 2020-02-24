package com.undertow.standalone;

import com.config.ContextLoaderListenerInstanceFactory;
import com.config.DispatcherServletInstanceFactory;
import com.server.Server;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.GracefulShutdownHandler;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.StuckThreadDetectionHandler;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.server.handlers.resource.ResourceHandler;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.ListenerInfo;
import io.undertow.servlet.api.ServletInfo;
import io.undertow.websockets.WebSocketConnectionCallback;
import io.undertow.websockets.WebSocketProtocolHandshakeHandler;
import io.undertow.websockets.core.AbstractReceiveListener;
import io.undertow.websockets.core.BufferedTextMessage;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.core.WebSockets;
import io.undertow.websockets.spi.WebSocketHttpExchange;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.util.cloud.DeploymentConfiguration.getProperty;
import static io.undertow.Handlers.websocket;
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

    private static ServletInfo createSpringServlet() {

        return servlet("DispatcherServlet", DispatcherServlet.class,
                new DispatcherServletInstanceFactory())
                .addMapping("/")
                .setLoadOnStartup(1)
                .setAsyncSupported(true);
    }

    private static ListenerInfo createContextLoaderListener() {
        return new ListenerInfo(ContextLoaderListener.class,
                new ContextLoaderListenerInstanceFactory());
    }

    private HttpHandler bootstrap() throws ServletException {
        final DeploymentInfo servletBuilder = deployment()
                .setClassLoader(Server.class.getClassLoader())
                .setContextPath("/")
                .addListeners(createContextLoaderListener())
                .setResourceManager(new ClassPathResourceManager(Server.class.getClassLoader(), "webapp/resources"))
                .addWelcomePage("index.html")
                .setDeploymentName(deploymentName)
                .addServlets(createSpringServlet());

        final DeploymentManager manager = defaultContainer().addDeployment(servletBuilder);
        manager.deploy();

        //Servlet handler
        final HttpHandler servletHandler = manager.start();

        //Open API resource handler
        final ResourceHandler resourceHandler = new ResourceHandler(new ClassPathResourceManager(Server.class.getClassLoader(), "apidoc"))
                .addWelcomeFiles("index.html")
                .setDirectoryListingEnabled(false);

        //Websocket handler
        final WebSocketProtocolHandshakeHandler chatHandler = websocket(new WebSocketConnectionCallback() {

            @Override
            public void onConnect(WebSocketHttpExchange exchange, WebSocketChannel channel) {
                channel.getReceiveSetter().set(new AbstractReceiveListener() {

                    @Override
                    protected void onFullTextMessage(WebSocketChannel channel, BufferedTextMessage message) {
                        final String messageData = message.getData();
                        for (WebSocketChannel session : channel.getPeerConnections()) {
                            WebSockets.sendText(messageData, session, null);
                        }
                    }
                });
                channel.resumeReceives();
            }
        });

        final PathHandler pathHandler = Handlers.path()
                .addPrefixPath("/", servletHandler)
                .addPrefixPath("apidoc", resourceHandler)
                .addPrefixPath("/chat", chatHandler);

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
    }


    public void stop() {
        server.stop();
        LOCK.unlock();
    }

}
