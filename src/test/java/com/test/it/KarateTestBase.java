package com.test.it;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;

import java.util.Optional;

@Slf4j
public abstract class KarateTestBase {

    public abstract void runAPITests();

    @BeforeAll
    public static void beforeAll() {
        log.info("beforeAll() was called.");
        checkVars();
        //TODO Call a cleanup .feature file?
    }

    private static void checkVars() {
        Optional<String> env = Optional.ofNullable(System.getProperty("env"));
        if (!env.isPresent()) {
            System.setProperty("env", "undefined");
        }
    }

}
