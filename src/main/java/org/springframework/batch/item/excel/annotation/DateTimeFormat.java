package org.springframework.batch.item.excel.annotation;

import java.lang.annotation.*;
import java.time.LocalDateTime;

/**
 * @Project: payment_reconciliation
 * @Package: com.wisdom.framwork.batch.excel.annotation
 * @Author: Designer
 * @CreateTime: 2019-11-19 13
 * @Description: 时间格式化
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface DateTimeFormat {

    /**
     * 日期格式
     *
     * @return
     */
    String formatter() default "yyyy-MM-dd HH-mm-ss";

    /**
     * 数据从EXCEL读取出来是什么类型
     *
     * @return
     */
    Class<?> originalType() default Long.class;

    /**
     * 读取出来的时间是什么格式
     * 1如果为2019-01-01则必须设置为LocalDate
     * 2如果为23：11：11则必须设置为LocalTime
     * 3如果为2019-01-01 23：11：11则必须设置为LocalDateTime
     *
     * @return
     */
    Class<?> targetType() default LocalDateTime.class;
}
