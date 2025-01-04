package org.codingchallenges.lb.backend.health;

import org.codingchallenges.lb.LoadBalancerMain;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class HealthCheckService {

    private static final Map<String, Boolean> serverHealthStatus = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        List<String> serversList = LoadBalancerMain.getServersList();
        serversList.forEach(server -> serverHealthStatus.put(server, true));
        ScheduledExecutorService healthCheckExecutor = Executors.newScheduledThreadPool(1);
        healthCheckExecutor.scheduleAtFixedRate(HealthCheckService::performHealthChecks, 0, 10, TimeUnit.SECONDS);
    }

    public void run() {
        main(null);
    }

    private static void performHealthChecks() {
        List<String> serversList = LoadBalancerMain.getServersList();
        List<CompletableFuture<Void>> healthFutures = new ArrayList<>();
        for (String server : serversList) {
            CompletableFuture<Void> healthFuture = CompletableFuture.runAsync(() -> {
                try {
                    URL url = new URL(server);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(2000);
                    connection.setReadTimeout(2000);
                    int responseCode = connection.getResponseCode();
                    serverHealthStatus.put(server, responseCode == 200);
                    connection.disconnect();
                } catch (Exception e) {
                    serverHealthStatus.put(server, false);
                    System.err.println("Exception happened while connecting to server : " + server + " : " + e.getMessage());
                }
            });
            healthFutures.add(healthFuture);
        }
        CompletableFuture.allOf(healthFutures.toArray(new CompletableFuture[0])).join();
        System.out.println("Health status completed : " + serverHealthStatus);
    }

    public static List<String> getHealthyServers() {
        List<String> healthyServers = new ArrayList<>();
        serverHealthStatus.forEach((key, value) -> {
            if (value) {
                healthyServers.add(key);
            }
        });
        return healthyServers;
    }
}
