package org.springframework.batch.item.excel.mapping;


import lombok.extern.log4j.Log4j2;
import org.springframework.batch.item.excel.RowMapper;

/**
 * @Project: payment_reconciliation
 * @Package: com.wisdom.framework.batch.excel.mapping
 * @Author: Designer
 * @CreateTime: 2019-11-19 15
 * @Description: excel数据转换成实体类，建议使用
 */
@Log4j2
@Deprecated
public abstract class ExcelBeanRowMapper<T> implements RowMapper<T> {


}
