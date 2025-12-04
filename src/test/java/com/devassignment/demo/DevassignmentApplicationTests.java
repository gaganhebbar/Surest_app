package com.devassignment.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.Mockito.mockStatic;


class DevassignmentApplicationTests {

    @Test
    void testMainMethod() {
        try (var mocked = mockStatic(SpringApplication.class)) {

            // Call the main method
            DevassignmentApplication.main(new String[]{});

            // Verify that SpringApplication.run() was called
            mocked.verify(() ->
                    SpringApplication.run(DevassignmentApplication.class, new String[]{}));
        }
    }

}
