package me.frochy.service;

import lombok.ToString;
import me.frochy.spring.*;

@ToString
@Component
@LazyInit
@Scope
public class UserService implements BeanNameAware, InitializingBean {

    @Autowired
    private OrderService orderService;
    private String beanName;

    public void test() {
        System.out.println("hello world!");
        System.out.println(orderService);
    }


    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    @Override
    public void afterPropertiesSet() {
        System.out.println("spring调用InitializingBean");
    }
}
