package ru.danperad.effectivemobiletest;

import org.springframework.boot.SpringApplication;

public class TestEffectiveMobileTestApplication {

    public static void main(String[] args) {
        SpringApplication
                .from(EffectiveMobileTestApplication::main)
                .with(TestcontainersConfiguration.class)
                .run(args);
    }

}
