package ethereum.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import ethereum.services.EthereumService;
import ethereum.services.impl.DefaultEthereumService;

@ComponentScan("ethereum")
public class EthereumConfig {
    @Bean
    public EthereumService ethereumService() {
        return new DefaultEthereumService();
    }
}
