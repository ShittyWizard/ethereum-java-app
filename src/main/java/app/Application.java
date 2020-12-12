package app;

import java.util.Collections;

import app.config.CorsConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

import ethereum.config.EthereumConfig;
import ipfs.config.IPFSConfig;


@Import({
        EthereumConfig.class,
        IPFSConfig.class,
        CorsConfiguration.class
})
@PropertySource(
        "classpath:ethereum.properties"
//        "classpath:ipfs.properties"
)
@SpringBootApplication
public class Application {
    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        LOG.info("Try to start Spring boot application...");
        SpringApplication application = new SpringApplication(Application.class);
        application.setDefaultProperties(Collections.singletonMap("server.port", "8081"));
        application.run(args);
        LOG.info("Successful start of spring boot application...");
    }
}
