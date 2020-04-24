package org.springframework.batch.item.excel.mapping;

import com.wisdom.common.utils.reflect.ReflectUtils;
import com.wisdom.common.utils.time.DateTimeUtils;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.item.excel.RowMapper;
import org.springframework.batch.item.excel.annotation.ColumnHeader;
import org.springframework.batch.item.excel.annotation.DateTimeFormat;
import org.springframework.batch.item.excel.support.rowset.RowSet;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Project: payment_reconciliation
 * @Package: com.wisdom.framework.batch.excel.mapping
 * @Author: Designer
 * @CreateTime: 2019-11-19 15
 * @Description: excel数据转换成实体类，建议使用
 */
@Log4j2
public class ExcelBeanRowMapper<T> implements RowMapper<T> {

    /**
     * MODEL对应的EXCEL头信息
     */
    private static final Map<String, String[]> CLASS_EXCEL_HEADER_MAP = new ConcurrentHashMap<>(4);


    private Class<? extends T> targetClass;

    private Integer shadowSkipConditionRow;

    /**
     * @param targetClass            被映射成集合的对象
     * @param shadowSkipConditionRow 判断改行是否为空，为空则不是有效数据
     */
    @NonNull
    public ExcelBeanRowMapper(Class<? extends T> targetClass, Integer shadowSkipConditionRow) {
        this.targetClass = targetClass;
        this.shadowSkipConditionRow = shadowSkipConditionRow;
    }

    public static String[] cache(Class<?> targetClass, String[] currentRow) {
        Assert.notNull(targetClass, "targetClass");
        return CLASS_EXCEL_HEADER_MAP.putIfAbsent(targetClass.getName(), currentRow);
    }

    @Override
    public T mapRow(final RowSet rs) throws Exception {
        String[] rowData = rs.getCurrentRow();
        if (rowData == null || rowData.length < shadowSkipConditionRow) {
            throw new RuntimeException("读取到了错误的EXCEL行");
        }
        if (StringUtils.isEmpty(rowData[shadowSkipConditionRow].trim())) {
            return null;
        }
        Assert.notNull(targetClass, "target class is null");
        String[] excelHeader = CLASS_EXCEL_HEADER_MAP.get(targetClass.getName());
        //解析类文件，将字段名字和字段上的EXCEL。HEADER名一一匹配对应
        Map<String, String> excelHeadToFieldMapper = new ClassFieldMapper().convertExcelHeadToField(targetClass);
        //空集合,存放类字段名
        String[] sortedField = new String[excelHeader.length];
        for (int i = 0, j = excelHeader.length; i < j; i++) {
            String fieldName = excelHeadToFieldMapper.get(excelHeader[i]);
            sortedField[i] = fieldName;
        }
        Map<String, Object> map = new LinkedHashMap<>();

        //将类的字段值填充满
        int i = 0;
        for (int j = sortedField.length; i < j; i++) {
            if (StringUtils.isEmpty(sortedField[i])) {
                continue;
            }
            Field field = targetClass.getDeclaredField(sortedField[i]);
            DateTimeFormat annotation = field.getAnnotation(DateTimeFormat.class);
            try {
                //字符串转数字类型
                if (int.class.equals(field.getType()) || Integer.class.equals(field.getType())) {
                    map.put(sortedField[i], StringUtils.isEmpty(rowData[i]) ? 0 : Integer.parseInt(String.valueOf(rowData[i])));
                } else if (BigDecimal.class.equals(field.getType())) {
                    map.put(sortedField[i], (StringUtils.isEmpty(rowData[i])) ? BigDecimal.valueOf(0) : new BigDecimal(rowData[i]));
                } else if (long.class.equals(field.getType()) || Long.class.equals(field.getType())) {
                    map.put(sortedField[i], StringUtils.isEmpty(rowData[i]) ? 0L : Long.parseLong(String.valueOf(rowData[i])));
                } else if (float.class.equals(field.getType()) || Float.class.equals(field.getType())) {
                    map.put(sortedField[i], StringUtils.isEmpty(rowData[i]) ? 0F : Float.parseFloat(String.valueOf(rowData[i])));
                } else if (double.class.equals(field.getType()) || Double.class.equals(field.getType())) {
                    map.put(sortedField[i], StringUtils.isEmpty(rowData[i]) ? 0D : Double.parseDouble(String.valueOf(rowData[i])));
                } else if (LocalDateTime.class.equals(field.getType())) {
                    if (annotation.originalType().equals(Long.class)) {
                        LocalDateTime localDateTime = DateTimeUtils.parseLongToDateTime(Long.parseLong(rowData[i]));
                        map.put(sortedField[i], localDateTime);
                    } else if (annotation.originalType().equals(String.class)) {
                        if (annotation.targetType().equals(LocalDate.class)) {
                            map.put(sortedField[i], DateTimeUtils.parseStrDateToLocalDateTime(rowData[i], annotation.formatter()));
                        } else if (annotation.targetType().equals(LocalDateTime.class)) {
                            map.put(sortedField[i], DateTimeUtils.parseStrDateTimeToLocalDateTime(rowData[i], annotation.formatter()));
                        } else if (annotation.targetType().equals(LocalTime.class)) {
                            //FIXME .atDate
                            map.put(sortedField[i], LocalTime.parse(rowData[i], DateTimeFormatter.ofPattern(annotation.formatter())).atDate(LocalDate.now()));
                        }
                    } else {
                        log.warn("未知的日期格式转换,class:{}, field:{}, fieldValue:{}", targetClass.getName(), field.getName(), rowData[i]);
                        map.put(sortedField[i], rowData[i]);
                    }
                } else {
                    map.put(sortedField[i], rowData[i]);
                }
            } catch (Exception e) {
                log.error(e);
                throw new ParseException(String.format("Excel第%s行数据转换失败:class: %s, field: %s,  fieldValue: %s", rs.getCurrentRowIndex(), targetClass.getName(), field.getName(), rowData[i]), rs.getCurrentRowIndex());
            }
        }
        return ReflectUtils.mapToBean(map, targetClass);
    }


    public static class ClassFieldMapper {

        Map<String, String> convertExcelHeadToField(Class<?> clazz) {

            Map<String, String> excelHeadToFiledMapper = new HashMap<>(16);
            //字段数组
            Field[] declaredFields = clazz.getDeclaredFields();
            for (Field field : declaredFields) {
                ColumnHeader annotation = field.getAnnotation(ColumnHeader.class);
                if (annotation != null) {
                    excelHeadToFiledMapper.put(annotation.headName(), field.getName());
                }
            }
            return excelHeadToFiledMapper;
        }

    }
}
