package me.frochy.spring;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.beans.Introspector;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
public class FrochyApplicationContext {
    private final String SUFFIX_DOT_CLASS = ".class";
    private final String SEPARATOR_DOT = ".";
    private Class configClass;

    private static final Map<String, BeanDefinition> BEAN_DEFINITION_MAP = new ConcurrentHashMap<>(16);
    private static Map<String, Object> BEAN_MAP = new ConcurrentHashMap<>(16);

    private List<BeanPostProcessor> beanPostProcessorList = new ArrayList<>();

    public FrochyApplicationContext(Class configClass) {
        this.configClass = configClass;

        if (configClass.isAnnotationPresent(ComponentScan.class)) {
            ComponentScan componentScan = (ComponentScan) configClass.getAnnotation(ComponentScan.class);
            String basePackages = componentScan.value();

            String finalBasePackages = basePackages.replace(SEPARATOR_DOT, "/");

            ClassLoader classLoader = this.getClass().getClassLoader();
            URL url = classLoader.getResource(finalBasePackages);

            File file = new File(url.getFile());
            log.info("配置路径扫描：{}", file);

            List<File> files = searchFile(file, SUFFIX_DOT_CLASS);

            files.forEach(f -> {
                String path = f.getPath();
                String classPath = path.substring(file.getPath().length() - finalBasePackages.length(), path.indexOf(SUFFIX_DOT_CLASS)).replace(File.separator, SEPARATOR_DOT);

                try {
                    Class<?> loadClass = classLoader.loadClass(classPath);
                    if (loadClass.isAnnotationPresent(Component.class)) {

                        if (BeanPostProcessor.class.isAssignableFrom(loadClass)) {
                            try {
                                BeanPostProcessor o = ((BeanPostProcessor) loadClass.newInstance());
                                beanPostProcessorList.add(o);
                            } catch (InstantiationException | IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        BeanDefinition beanDefinition = new BeanDefinition();
                        beanDefinition.setType(loadClass);

                        DefinitionInitChain initChanin;
                        initChanin = new ScopeInitChain();
                        initChanin = new LazyInitChain(initChanin);

                        initChanin.process(beanDefinition);

                        BEAN_DEFINITION_MAP.put(getBeanName(loadClass), beanDefinition);
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            });
        }

        BEAN_MAP = BEAN_DEFINITION_MAP.entrySet().stream()
                .filter(sb -> sb.getValue().getScope().equals("singleton") && sb.getValue().getLazyInit().equals(Boolean.FALSE))
                .collect(Collectors.toConcurrentMap(Map.Entry::getKey, sb -> createBean(sb.getValue())));

    }

    private Object createBean(BeanDefinition beanDefinition) {
        Class type = beanDefinition.getType();
        try {
            Object instance = type.getConstructor().newInstance();

            //依赖注入
            Arrays.stream(type.getDeclaredFields())
                    .filter(f -> f.isAnnotationPresent(Autowired.class))
                    .forEach(f -> {
                        f.setAccessible(true);
                        try {
                            f.set(instance, getBean(f.getName()));
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    });

            // Aware回调
            if (instance instanceof BeanNameAware) {
                ((BeanNameAware) instance).setBeanName(getBeanName(type));
            }

            //postProcessBeforeInitialization
            beanPostProcessorList.forEach(beanPostProcessor -> beanPostProcessor.postProcessBeforeInitialization(getBeanName(type), instance));

            // 初始化
            if (instance instanceof InitializingBean) {
                ((InitializingBean) instance).afterPropertiesSet();
            }

            //postProcessAfterInitialization
            beanPostProcessorList.forEach(beanPostProcessor -> beanPostProcessor.postProcessAfterInitialization(getBeanName(type), instance));

            // AOP

            return instance;
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getBeanName(Class<?> loadClass) {
        Component componentAnnotation = loadClass.getAnnotation(Component.class);
        if (StringUtils.isNotBlank(componentAnnotation.value())) {
            return componentAnnotation.value();
        }

        return Introspector.decapitalize(loadClass.getSimpleName());
    }

    /**
     * 递归查找包含关键字的文件
     *
     * @param folder  basePackages
     * @param keyWord 截取关键字
     * @return 包含关键字的文件列表
     */
    public static List<File> searchFile(File folder, final String keyWord) {
        List<File> fileList = Arrays.stream(Objects.requireNonNull(folder.listFiles(pathname -> pathname.isDirectory()
                || (pathname.isFile() && pathname.getName().toLowerCase().contains(keyWord.toLowerCase()))))).toList();

        List<File> result = new ArrayList<>();

        fileList.forEach(file -> {
            if (file.isFile()) {
                result.add(file);
            } else {
                List<File> foldResult = searchFile(file, keyWord);
                result.addAll(foldResult);
            }
        });

        return result;
    }

    public Object getBean(String beanName) {
        BeanDefinition beanDefinition = BEAN_DEFINITION_MAP.get(beanName);

        String scope = beanDefinition.getScope();

        if (scope.equals("singleton")) {
            Object o = BEAN_MAP.get(beanName);
            if (o == null) {
                o = createBean(BEAN_DEFINITION_MAP.get(beanName));
                BEAN_MAP.put(beanName, o);
            }
            return o;
        }

        return createBean(BEAN_DEFINITION_MAP.get(beanName));
    }

    public <T> T getBean(Class<T> type) {
        return (T) getBean(getBeanName(type));
    }
}
