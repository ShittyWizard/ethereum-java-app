package ipfs.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import ipfs.services.IPFSService;
import ipfs.services.impl.DefaultIPFSService;

@ComponentScan("ipfs")
public class IPFSConfig {
    @Bean
    public IPFSService ipfsService() {
        return new DefaultIPFSService();
    }
}
