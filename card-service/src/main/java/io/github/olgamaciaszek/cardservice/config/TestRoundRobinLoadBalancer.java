package io.github.olgamaciaszek.cardservice.config;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import reactor.core.publisher.Mono;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.reactive.DefaultResponse;
import org.springframework.cloud.client.loadbalancer.reactive.EmptyResponse;
import org.springframework.cloud.client.loadbalancer.reactive.Request;
import org.springframework.cloud.client.loadbalancer.reactive.Response;
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;

/**
 * A copy of https://github.com/spring-cloud/spring-cloud-commons/blob/master/spring-cloud-loadbalancer/src/main/java/org/springframework/cloud/loadbalancer/core/RoundRobinLoadBalancer.java
 * with some additional logging added, just to demo passing alternative LB configurations
 *
 * @author Olga Maciaszek-Sharma
 */
public class TestRoundRobinLoadBalancer implements ReactorServiceInstanceLoadBalancer {

	private static final Log LOG = LogFactory.getLog(TestRoundRobinLoadBalancer.class);

	private final AtomicInteger position;

	private final ObjectProvider<ServiceInstanceListSupplier> serviceInstanceSupplier;

	private final String serviceId;

	public TestRoundRobinLoadBalancer(String serviceId,
			ObjectProvider<ServiceInstanceListSupplier> serviceInstanceSupplier) {
		this(serviceId, serviceInstanceSupplier, new Random().nextInt(1000));
	}

	private TestRoundRobinLoadBalancer(String serviceId,
			ObjectProvider<ServiceInstanceListSupplier> serviceInstanceSupplier,
			int seedPosition) {
		LOG.info(TestRoundRobinLoadBalancer.class.getSimpleName() + " instance created.");
		this.serviceId = serviceId;
		this.serviceInstanceSupplier = serviceInstanceSupplier;
		this.position = new AtomicInteger(seedPosition);
	}

	@Override
	// see original
	// https://github.com/Netflix/ocelli/blob/master/ocelli-core/
	// src/main/java/netflix/ocelli/loadbalancer/RoundRobinLoadBalancer.java
	public Mono<Response<ServiceInstance>> choose(Request request) {
		LOG.info("Using " + TestRoundRobinLoadBalancer.class
				.getSimpleName() + " to retrieve a service instance.");
		ServiceInstanceListSupplier supplier = this.serviceInstanceSupplier
				.getIfAvailable();
		return supplier.get().next().map(instances -> {
			if (instances.isEmpty()) {
				LOG.warn("No servers available for service: " + this.serviceId);
				return new EmptyResponse();
			}
			int pos = Math.abs(this.position.incrementAndGet());

			ServiceInstance instance = instances.get(pos % instances.size());

			return new DefaultResponse(instance);
		});
	}
}
