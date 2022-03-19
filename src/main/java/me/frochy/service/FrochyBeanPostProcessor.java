package me.frochy.service;

import me.frochy.spring.BeanPostProcessor;
import me.frochy.spring.Component;

@Component
public class FrochyBeanPostProcessor implements BeanPostProcessor {
    @Override
    public void postProcessBeforeInitialization(String beanName, Object bean) {
        if (beanName.equals("userService")) {
            System.out.println("调用postProcessBeforeInitialization");
        }
    }

    @Override
    public void postProcessAfterInitialization(String beanName, Object bean) {
        if (beanName.equals("userService")) {
//            Object o = Proxy.newProxyInstance(FrochyBeanPostProcessor.class.getClassLoader(), bean.getClass().getInterfaces(), new InvocationHandler() {
//                @Override
//                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//                    System.out.println("切面逻辑");
//                    return null;
//                }
//            });
            System.out.println("调用postProcessAfterInitialization");
//            return o;
        }
//        return bean;
    }
}
