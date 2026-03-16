package org.kate.springconfigserver2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
public class SpringConfigServer2Application {

    public static void main(String[] args) {
        SpringApplication.run(SpringConfigServer2Application.class, args);
    }

}
