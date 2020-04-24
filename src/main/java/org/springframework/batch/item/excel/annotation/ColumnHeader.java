package org.springframework.batch.item.excel.annotation;

import java.lang.annotation.*;

/**
 * @Project: payment_reconciliation
 * @Package: com.wisdom.framwork.batch.excel.annotation
 * @Author: Designer
 * @CreateTime: 2019-11-19 14
 * @Description: 批处理，数据头注解
 * 该注解其实没什么用，主要是为了给添加字段注释，方便查看。
 * StreamReader可以
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface ColumnHeader {

    /**
     * 头部的列名
     *
     * @return
     */
    String headName();

    /**
     * 头部列名的下标
     *
     * @return
     */
    int headIndex() default -1;
}
