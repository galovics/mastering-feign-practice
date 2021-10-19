package com.arnoldgalovics.online.store.service;

import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

public class TestServiceInstanceListSupplier implements ServiceInstanceListSupplier {
    private final String name;
    private final int[] ports;

    public TestServiceInstanceListSupplier(String name, int... ports) {
        this.name = name;
        this.ports = ports;
    }

    @Override
    public String getServiceId() {
        return name;
    }

    @Override
    public Flux<List<ServiceInstance>> get() {
        List<ServiceInstance> serviceInstances = new ArrayList<>();
        for (int i = 0; i < ports.length; i++) {
            serviceInstances.add(new DefaultServiceInstance(name + "-" + i, getServiceId(), "localhost", ports[i], false));
        }

        return Flux.just(serviceInstances);
    }
}
