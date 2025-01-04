package org.codingchallenges.lb.strategy;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class WeightedRoundRobinStrategy implements LoadBalancingStrategy {

    private final AtomicInteger currentIndex = new AtomicInteger(-1);
    private final AtomicInteger currentWeight = new AtomicInteger(0);

    private final Map<String, Integer> serverWeights;

    public WeightedRoundRobinStrategy(Map<String, Integer> serverWeights) {
        this.serverWeights = serverWeights;
    }

    @Override
    public String getServerToSendRequest(List<String> servers, String clientIP) {
        if (servers.isEmpty()) {
            throw new IllegalArgumentException("No servers available");
        }
        if (currentIndex.get() == -1 || currentWeight.get() == 0) {
            int index = currentIndex.incrementAndGet() % servers.size();
            int newWeight = serverWeights.getOrDefault(servers.get(index), 1);
            currentWeight.set(newWeight);
            currentIndex.set(index);
        }
        currentWeight.decrementAndGet();
        return servers.get(currentIndex.get());
    }

    public static void main(String[] args) {
        List<String> servers = Arrays.asList("Server1", "Server2", "Server3");
        // Define weights for the servers
        Map<String, Integer> serverWeights = Map.of(
                "Server1", 5,
                "Server2", 3,
                "Server3", 2
        );
        LoadBalancingStrategy lb = new WeightedRoundRobinStrategy(serverWeights);
        for (int i = 0; i < 14; i++) {
            System.out.println(lb.getServerToSendRequest(servers, null));
        }
    }

}
