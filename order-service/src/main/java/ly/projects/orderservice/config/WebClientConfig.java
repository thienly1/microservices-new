package ly.projects.orderservice.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    /*@Bean
    public WebClient webClient(){
        return WebClient.builder().build();
    }*/
    //modify the method above to the following method to fix the error: Failed to resolve "inventory-service". It will check over each port
    @Bean
    @LoadBalanced
    public WebClient.Builder webClientBuilder(){
        return WebClient.builder();
    }

}
