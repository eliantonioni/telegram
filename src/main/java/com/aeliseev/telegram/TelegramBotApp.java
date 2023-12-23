package com.aeliseev.telegram;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.aeliseev.telegram", "org.telegram"})
public class TelegramBotApp {

    public static void main(String[] args) {
        SpringApplication.run(TelegramBotApp.class, args);
    }

}
