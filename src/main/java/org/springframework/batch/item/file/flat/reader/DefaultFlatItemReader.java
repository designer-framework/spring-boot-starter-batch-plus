package org.springframework.batch.item.file.flat.reader;


import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.StringUtils;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.util.Arrays;
import java.util.Objects;

/**
 * @Project: payment_reconciliation
 * @Package: com.wisdom.reconciliation.config.configuration.batch.process
 * @Author: Designer
 * @CreateTime: 2020-01-06 14
 * @Description: Flat文件读取
 */
@Log4j2
public class DefaultFlatItemReader<T> extends FlatFileItemReader<T> {


    /**
     * @param resource   源文件
     * @param clazz
     * @param lineToSkip
     */
    public DefaultFlatItemReader(Resource resource, Class<? extends T> clazz, DefaultLineMapper<T> defaultLineMapper, int lineToSkip) {
        Assert.notNull(clazz, "field targetType must be not null");
        setLinesToSkip(lineToSkip);
        setLineMapper(getLineMapper(defaultLineMapper, clazz));
        setResource(resource);
    }

    public LineMapper<T> getLineMapper(DefaultLineMapper<T> tDefaultLineMapper, Class<? extends T> clazz) {
        Assert.notNull(tDefaultLineMapper, "");
        Assert.notNull(clazz, "");
        tDefaultLineMapper.setLineTokenizer(getDelimitedLineTokenizer(clazz));
        tDefaultLineMapper.setFieldSetMapper(getFieldSetMapper(clazz));
        return tDefaultLineMapper;
    }


    public FieldSetMapper<T> getFieldSetMapper(Class<? extends T> clazz) {
        BeanWrapperFieldSetMapper<T> tBeanWrapperFieldSetMapper = new BeanWrapperFieldSetMapper<>();
        ApplicationContext applicationContext = Objects.requireNonNull(SpringContextUtils1.applicationContext);
        tBeanWrapperFieldSetMapper.setBeanFactory(applicationContext);
        String beanName = replaceFirstChar(ClassUtils.getShortName(clazz));
        try {
            applicationContext.getBean(beanName);
        } catch (BeansException e) {
            log.error("查找Bean失败,可能" + beanName + "未注入到容器中", e);
            throw new RuntimeException(e);
        }
        tBeanWrapperFieldSetMapper.setPrototypeBeanName(beanName);
        return tBeanWrapperFieldSetMapper;
    }

    /**
     * @param clazz 实体类
     * @return
     */
    LineTokenizer getDelimitedLineTokenizer(Class<? extends T> clazz) {
        String[] delimitedLineTokenizerNames = getDelimitedLineTokenizerNames(clazz);
        //数据分割核心配置类
        DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer(getDelimiter());
        delimitedLineTokenizer.setNames(delimitedLineTokenizerNames);
        return delimitedLineTokenizer;
    }

    /**
     * 字段顺序对应csv文件的表头顺序
     *
     * @param clazz
     * @return
     */
    public String[] getDelimitedLineTokenizerNames(Class<? extends T> clazz) {
        String[] names = Arrays.stream(clazz.getDeclaredFields()).filter(field ->
                !("serialVersionUID".equals(field.getName()))
        ).map(field -> {
            field.setAccessible(true);
           /* ColumnHeader columnHeader = field.getAnnotation(ColumnHeader.class);
            if (columnHeader != null) {
                return columnHeader.headName();
            }*/
            return field.getName();
        }).toArray(String[]::new);
        return names;
    }

    public String getDelimiter() {
        return ",";
    }

    private String replaceFirstChar(String str) {
        if (StringUtils.isNotEmpty(str)) {
            char oldChar = str.charAt(0);
            if (oldChar >= 'A' && oldChar <= 'Z') {
                return str.replace(oldChar, (char) (oldChar + 32));
            } else if (oldChar >= 'a' && oldChar <= 'z') {
                return str.replace(oldChar, (char) (oldChar - 32));
            } else {
                return str;
            }
        }
        return str;
    }

}
