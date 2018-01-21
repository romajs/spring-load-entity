package com.romajs.spring.annotation.processor;

import com.romajs.spring.annotation.util.LoadEntityFieldCallback;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Component
public class LoadEntityBeanPostProcessor implements BeanPostProcessor {

    @Autowired
    private ConfigurableListableBeanFactory configurableBeanFactory;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        ReflectionUtils.FieldCallback fieldCallback = new LoadEntityFieldCallback(configurableBeanFactory, entityManager, bean);
        ReflectionUtils.doWithFields(bean.getClass(), fieldCallback);
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

}