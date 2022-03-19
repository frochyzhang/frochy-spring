package me.frochy.spring;

import lombok.Data;

@Data
public class ScopeInitChain implements DefinitionInitChain {

    private DefinitionInitChain initChain;

    @Override
    public void process(BeanDefinition beanDefinition) {
        if (initChain != null) {
            initChain.process(beanDefinition);
        }

        if (beanDefinition.getType().isAnnotationPresent(Scope.class)) {
            beanDefinition.setScope("prototype");
        }
    }
}
