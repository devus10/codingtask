package com.coding.codingtask;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.Scanner;

@SpringBootApplication
@Slf4j
public class Application implements CommandLineRunner {

    @Autowired
    private ApplicationContext applicationContext;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        while (true) {
            Scanner scanner = new Scanner(System.in);

            System.out.println("Type log file path");
            String logFilePath = scanner.nextLine();

            System.out.println("Log file path: " + logFilePath);
            saveEvents(logFilePath);

            System.out.println("0 - exit, other - continue");
            String command = scanner.nextLine();
            if (command.equals("0")) {
                return;
            }
        }
    }

    private void saveEvents(String logFilePath) {
        try {
            EventService eventService = (EventService) applicationContext.getBean("eventService");
            eventService.saveEventsFromLogFile(logFilePath);
        } catch (Exception e) {
            log.info(e.getMessage());
        }

    }

    @Bean
    ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
