package me.frochy;

import me.frochy.service.UserService;
import me.frochy.service.config.AppConfig;
import me.frochy.spring.FrochyApplicationContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AppConfigTest {

    @Test
    public void appTest() {
        FrochyApplicationContext applicationContext = new FrochyApplicationContext(AppConfig.class);
        UserService userService = applicationContext.getBean(UserService.class);
        Assertions.assertNotNull(userService.getBeanName());
    }
}
