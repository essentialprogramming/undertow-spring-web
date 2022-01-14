package com.server;

import com.undertow.standalone.UndertowServer;
import javax.servlet.ServletException;
import static com.util.cloud.Environment.getProperty;

public class Server {

    public static void main(String[] args) throws ServletException {

        final String host = getProperty("undertow.host", "localhost");
        final Integer port = getProperty("undertow.port", 8080);

        final UndertowServer server = new UndertowServer(host, port, "undertow-spring-web.jar");
        server.start();
    }

}
