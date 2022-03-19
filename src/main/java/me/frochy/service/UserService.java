package me.frochy.service;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import me.frochy.spring.*;

@Slf4j
@ToString
@Component
@LazyInit
@Scope
public class UserService implements BeanNameAware, InitializingBean {

    @Autowired
    private OrderService orderService;
    private String beanName;

    public void test() {
        log.info("hello world!");
        log.info("orderService:{}",orderService);
    }


    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public String getBeanName() {
        return this.beanName;
    }
    @Override
    public void afterPropertiesSet() {
        log.info("spring调用InitializingBean");
    }
}
