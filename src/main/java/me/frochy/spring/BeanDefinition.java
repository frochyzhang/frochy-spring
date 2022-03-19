package me.frochy.spring;

import lombok.Data;

@Data
public class BeanDefinition {
    private Class type;
    private String scope = "singleton";
    private Boolean lazyInit = Boolean.FALSE;
}
