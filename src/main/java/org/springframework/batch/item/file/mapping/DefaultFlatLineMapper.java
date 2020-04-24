package org.springframework.batch.item.file.mapping;

import lombok.extern.log4j.Log4j2;

/**
 * @Project: wisdom
 * @Package: com.wisdom.framework.batch.file.mapping
 * @Author: Designer
 * @CreateTime: 2020-01-07 15
 * @Description:
 */
@Log4j2
public class DefaultFlatLineMapper<T> extends DefaultLineMapper<T> {

    @Override
    public T mapLine(String line, int lineNumber) throws Exception {
        line = processorLine(line, lineNumber);
        T t = null;
        //判断是否抵达尾部，未抵达尾部则进入代码块
        if (notFoot(line, lineNumber)) {
            t = super.mapLine(line, lineNumber);
        } else {
            log.warn("抵达尾行!第{}行,数据：{}", lineNumber, line);
        }
        return t;
    }

    /**
     * 在对截取的单条数据进行处理之前，可能需要对一些特殊字符串进行处理再映射成bean
     *
     * @param line
     * @param lineNumber
     * @return
     */
    public String processorLine(String line, int lineNumber) throws Exception {
        // exanple:  line = "1&,2&,3";
        // return line.replaceAll("&", "");  result: 1,2,3
        return line;
    }

    /**
     * 不是尾行则返回true
     *
     * @param line
     * @param lineNumber
     * @return
     */
    public boolean notFoot(String line, int lineNumber) {
        return true;
    }
}
