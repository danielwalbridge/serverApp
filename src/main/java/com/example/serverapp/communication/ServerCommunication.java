package com.example.serverapp.communication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ServerCommunication {

    @Autowired
    LoadBalancerClient loadBalancerClient;

    public String getConsoleInfo() {

        // get ServiceInstance list using serviceId
        ServiceInstance serviceInstance = loadBalancerClient.choose("CONSOLE-SERVICE");

        // endpoint // read URI and Add path that returns url
        String url = serviceInstance.getUri()+"/console/test";
        //RestTemplate create object for RestTemplate
        RestTemplate restTemplate = new RestTemplate();

        String response = restTemplate.getForObject(url, String.class);

        return response;
    }
}
