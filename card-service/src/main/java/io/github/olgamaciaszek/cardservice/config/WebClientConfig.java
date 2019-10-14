package io.github.olgamaciaszek.cardservice.config;

import io.github.olgamaciaszek.excluded.CustomLoadBalancerConfiguration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * @author Olga Maciaszek-Sharma
 */
@Configuration
@LoadBalancerClient(value = "ignored", configuration = CustomLoadBalancerConfiguration.class)
public class WebClientConfig {

	@Bean
	@LoadBalanced
	@Qualifier("loadBalancedWebClient")
	WebClient.Builder loadBalancedWebClientBuilder() {
		return WebClient.builder();
	}

	@Bean
	@Qualifier("webClient")
	WebClient.Builder webClientBuilder() {
		return WebClient.builder();
	}
}



