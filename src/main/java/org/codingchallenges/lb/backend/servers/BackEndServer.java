package org.codingchallenges.lb.backend.servers;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class BackEndServer {

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(Integer.parseInt(args[0])), 0);
        server.createContext("/", exchange -> {
            String response = "Hello from backend server : " + args[0];
            exchange.sendResponseHeaders(200, response.length());
            exchange.getResponseBody().write(response.getBytes());
            exchange.getResponseBody().close();
        });
        server.setExecutor(null);
        server.start();
        System.out.println("Server started on port : " + args[0]);
    }

}
