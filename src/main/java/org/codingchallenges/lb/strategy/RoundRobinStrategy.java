package org.codingchallenges.lb.strategy;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinStrategy implements LoadBalancingStrategy {

    private static final AtomicInteger counter = new AtomicInteger(0);

    @Override
    public String getServerToSendRequest(List<String> servers, String clientIP) {
        if (servers.isEmpty()) {
            throw new IllegalArgumentException("No servers available to select");
        }
        int index = counter.getAndUpdate(i -> (i + 1) % servers.size());
        return servers.get(index);
    }
}
