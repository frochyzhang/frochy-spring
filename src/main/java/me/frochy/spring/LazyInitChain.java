package me.frochy.spring;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LazyInitChain implements DefinitionInitChain {

    private DefinitionInitChain initChain;

    @Override
    public void process(BeanDefinition beanDefinition) {
        if (initChain != null) {
            initChain.process(beanDefinition);
        }

        if (beanDefinition.getType().isAnnotationPresent(LazyInit.class)) {
            beanDefinition.setLazyInit(Boolean.TRUE);
        }
    }
}
