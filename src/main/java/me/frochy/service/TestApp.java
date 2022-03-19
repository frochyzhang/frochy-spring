package me.frochy.service;

import lombok.extern.slf4j.Slf4j;
import me.frochy.service.config.AppConfig;
import me.frochy.spring.FrochyApplicationContext;

import java.io.IOException;

@Slf4j
public class TestApp {
    public static void main(String[] args) throws IOException {
        FrochyApplicationContext applicationContext = new FrochyApplicationContext(AppConfig.class);
        UserService userService = applicationContext.getBean(UserService.class);

        userService.test();
    }
}
