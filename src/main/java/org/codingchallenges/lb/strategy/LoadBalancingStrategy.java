package org.codingchallenges.lb.strategy;

import java.util.List;

public interface LoadBalancingStrategy {

    String getServerToSendRequest(List<String> servers, String clientIP);

}
