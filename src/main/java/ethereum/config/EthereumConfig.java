package ethereum.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;

import ethereum.services.EthereumService;
import ethereum.services.impl.DefaultEthereumService;

@ComponentScan("ethereum")
public class EthereumConfig {
    @Bean
    public EthereumService ethereumService() {
        return new DefaultEthereumService();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
