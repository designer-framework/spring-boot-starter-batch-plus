package org.springframework.batch.item.file.flat.reader;

import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * spring-boot-starter-batch-plus
 *
 * @author Designer
 * @version 1.0.0
 * @date 2020/4/24 18:24
 * @description
 */
@Configuration
public class DefaultBeanWrapperFieldSetMapper implements BeanFactoryAware {

    @Bean
    @Scope(value = "singleton")
    public BeanWrapperFieldSetMapper beanWrapperFieldSetMapper(){
        BeanWrapperFieldSetMapper beanWrapperFieldSetMapper = new BeanWrapperFieldSetMapper();
        beanWrapperFieldSetMapper.setBeanFactory(beanFactory);
        return beanWrapperFieldSetMapper;
    }

    private BeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

}
