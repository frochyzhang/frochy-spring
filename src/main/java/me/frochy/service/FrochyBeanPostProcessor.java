package me.frochy.service;

import lombok.extern.slf4j.Slf4j;
import me.frochy.spring.BeanPostProcessor;
import me.frochy.spring.Component;

@Slf4j
@Component
public class FrochyBeanPostProcessor implements BeanPostProcessor {
    @Override
    public void postProcessBeforeInitialization(String beanName, Object bean) {
        if (beanName.equals("userService")) {
            log.info("调用postProcessBeforeInitialization");
        }
    }

    @Override
    public void postProcessAfterInitialization(String beanName, Object bean) {
        if (beanName.equals("userService")) {
//            Object o = Proxy.newProxyInstance(FrochyBeanPostProcessor.class.getClassLoader(), bean.getClass().getInterfaces(), new InvocationHandler() {
//                @Override
//                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//                    log.info("切面逻辑");
//                    return null;
//                }
//            });
            log.info("调用postProcessAfterInitialization");
//            return o;
        }
//        return bean;
    }
}
