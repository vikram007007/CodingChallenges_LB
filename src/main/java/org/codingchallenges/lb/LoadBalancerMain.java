package org.codingchallenges.lb;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.codingchallenges.lb.backend.health.HealthCheckService;
import org.codingchallenges.lb.strategy.RoundRobinStrategy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class LoadBalancerMain {

    private static final String[] SERVERS = {
            "http://localhost:8080",
            "http://localhost:8081",
            "http://localhost:8082"
    };

    private static final List<String> SERVERS_LIST = new ArrayList<>(List.of(SERVERS));

    public static void main(String[] args) throws IOException {
        LoadBalancer loadBalancer = new LoadBalancer();
        loadBalancer.setStrategy(new RoundRobinStrategy());
        HealthCheckService healthCheckService = new HealthCheckService();
        healthCheckService.run();
        HttpServer server = HttpServer.create(new InetSocketAddress(80), 0);
        System.out.println("Started load balancer....");
        server.createContext("/", new ProxyHandler(loadBalancer));
        server.setExecutor(null);
        server.start();
    }

    public static List<String> getServersList() {
        return SERVERS_LIST;
    }

    static class ProxyHandler implements HttpHandler {

        private final LoadBalancer loadBalancer;

        public ProxyHandler(LoadBalancer loadBalancer) {
            this.loadBalancer = loadBalancer;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String targetServer = loadBalancer.getServer(HealthCheckService.getHealthyServers(), "");
            System.out.println("Forwarding request to : " + targetServer);

            URL targetURL = new URL(targetServer + exchange.getRequestURI());
            HttpURLConnection connection = (HttpURLConnection) targetURL.openConnection();
            connection.setRequestMethod(exchange.getRequestMethod());

            exchange.getRequestHeaders().forEach((key, values) -> {
                for (String value : values) {
                    connection.setRequestProperty(key, value);
                }
            });

            int responseCode = connection.getResponseCode();
            InputStream targetResponse = responseCode >= 400 ? connection.getErrorStream() : connection.getInputStream();
            byte[] responseBody = targetResponse.readAllBytes();

            exchange.sendResponseHeaders(responseCode, responseBody.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(responseBody);
            }
            connection.disconnect();
        }
    }
}