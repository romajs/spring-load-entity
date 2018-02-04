package romajs.spring.annotation.util;

import romajs.spring.annotation.LoadEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.util.ReflectionUtils;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import java.lang.reflect.Field;

public class LoadEntityFieldCallback implements ReflectionUtils.FieldCallback {

    private static Logger logger = LoggerFactory.getLogger(LoadEntityFieldCallback.class);

    private static int AUTOWIRE_MODE = AutowireCapableBeanFactory.AUTOWIRE_BY_NAME;

    private static String ERROR_ENTITY_ANNOTATION_NOT_PRESENT = "@LoadEntity(value) is not annotated with @Entity";
    private static String ERROR_CREATE_INSTANCE = "Cannot create instance of type '{}' or instance creation is failed because: {}";

    private final ConfigurableListableBeanFactory configurableBeanFactory;
    private final EntityManager entityManager;
    private final Object bean;

    public LoadEntityFieldCallback(ConfigurableListableBeanFactory configurableBeanFactory, EntityManager entityManager, Object bean) {
        this.configurableBeanFactory = configurableBeanFactory;
        this.entityManager = entityManager;
        this.bean = bean;
    }

    @Override
    public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {

        if (!field.isAnnotationPresent(LoadEntity.class)) {
            return;
        }

        ReflectionUtils.makeAccessible(field);

        Class<?> fieldType = field.getType();
        long idValue = field.getDeclaredAnnotation(LoadEntity.class).value();

        if (fieldType.isAnnotationPresent(Entity.class)) {
            String beanName = String.format("@%s[%s=%d]", LoadEntity.class.getSimpleName(),
                    fieldType.getSimpleName(), idValue);
            Object beanInstance = registerBeanInstance(beanName, fieldType, idValue);
            field.set(bean, beanInstance);
        } else {
            throw new IllegalArgumentException(ERROR_ENTITY_ANNOTATION_NOT_PRESENT);
        }
    }

    public Object registerBeanInstance(String beanName, Class<?> fieldType, Long idValue) {
        Object beanInstance;
        if (!configurableBeanFactory.containsBean(beanName)) {
            logger.info("Loading fixture to new bean named '{}'.", beanName);

            Object entityValue;
            try {
                entityValue = entityManager.find(fieldType, idValue);
            } catch (Exception e) {
                logger.error(ERROR_CREATE_INSTANCE, fieldType.getTypeName(), e);
                throw new RuntimeException(e);
            }

            beanInstance = configurableBeanFactory.initializeBean(entityValue, beanName);
            configurableBeanFactory.autowireBeanProperties(beanInstance, AUTOWIRE_MODE, true);
            configurableBeanFactory.registerSingleton(beanName, beanInstance);
            logger.info("Bean named '{}' created successfully.", beanName);
        } else {
            beanInstance = configurableBeanFactory.getBean(beanName);
            logger.info("Bean named '{}' already exists used as current bean reference.", beanName);
        }
        return beanInstance;
    }
}