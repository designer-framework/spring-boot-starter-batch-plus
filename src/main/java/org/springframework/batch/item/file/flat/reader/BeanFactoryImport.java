package org.springframework.batch.item.file.flat.reader;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

/**
 * spring-boot-starter-batch-plus
 *
 * @author Designer
 * @version 1.0.0
 * @date 2020/4/28 16:27
 * @description
 */

public class BeanFactoryImport implements BeanFactoryAware {

    static BeanFactory beanFactory;

    public BeanFactoryImport() {
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        BeanFactoryImport.beanFactory = beanFactory;
    }
}
