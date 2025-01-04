package org.codingchallenges.lb.strategy;

import java.util.List;
import java.util.Objects;

public class IPHashStrategy implements LoadBalancingStrategy {
    @Override
    public String getServerToSendRequest(List<String> servers, String clientIP) {
        if (servers.isEmpty()) {
            throw new IllegalArgumentException("No available servers");
        }
        if (Objects.isNull(clientIP) || clientIP.isEmpty()) {
            throw new IllegalArgumentException("Client IP is required");
        }
        int hash = Math.abs(clientIP.hashCode());
        return servers.get(hash % servers.size());
    }
}
