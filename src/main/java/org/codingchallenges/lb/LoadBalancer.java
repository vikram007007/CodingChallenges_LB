package org.codingchallenges.lb;

import org.codingchallenges.lb.strategy.LoadBalancingStrategy;

import java.util.List;

public class LoadBalancer {

    private LoadBalancingStrategy loadBalancingStrategy;

    public void setStrategy(LoadBalancingStrategy loadBalancingStrategy) {
        this.loadBalancingStrategy = loadBalancingStrategy;
    }

    public String getServer(List<String> servers, String clientIP) {
        if (loadBalancingStrategy == null) {
            throw new IllegalStateException("No load balancing strategy is set");
        }
        return loadBalancingStrategy.getServerToSendRequest(servers, clientIP);
    }

}
