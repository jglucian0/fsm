package dev.jgluciano.fsm.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "dev.jgluciano.fsm")
public class FsmApplication {

    public static void main(String[] args) {
        SpringApplication.run(FsmApplication.class, args);
    }
}
