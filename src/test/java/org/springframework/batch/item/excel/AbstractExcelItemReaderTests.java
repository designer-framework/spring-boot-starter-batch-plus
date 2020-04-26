/*
 * Copyright 2006-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.batch.item.excel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.excel.mapping.PassThroughRowMapper;
import org.springframework.batch.item.excel.support.rowset.RowSet;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.StringUtils;

import static org.junit.Assert.assertEquals;

/**
 * Base class for testing Excel based item readers.
 *
 * @author Marten Deinum
 */
public abstract class AbstractExcelItemReaderTests {

    protected final Log logger = LogFactory.getLog(getClass());

    protected AbstractExcelItemReader itemReader;

    private ExecutionContext executionContext;

    private String filePath;

    private int headerRowCount;

    public AbstractExcelItemReaderTests(String filePath, int headerRowCount) {
        this.filePath = filePath;
        this.headerRowCount = headerRowCount;
    }

    @Before
    public void setup() throws Exception {
        itemReader = createExcelItemReader();
        itemReader.setLinesToSkip(headerRowCount); //First line is column names
        itemReader.setResource(new ClassPathResource(filePath));
        // itemReader.setResource(new ClassPathResource("org/springframework/batch/item/excel/player.xls"));
        itemReader.setRowMapper(new PassThroughRowMapper());
        itemReader.setSkippedRowsCallback(new RowCallbackHandler() {

            @Override
            public void handleRow(RowSet rs) {
                logger.info("Skipping: " + StringUtils.arrayToCommaDelimitedString(rs.getCurrentRow()));
            }
        });
        configureItemReader(itemReader);
        itemReader.afterPropertiesSet();
        executionContext = new ExecutionContext();
        itemReader.open(executionContext);
    }

    protected void configureItemReader(AbstractExcelItemReader itemReader) {
    }

    @After
    public void after() throws Exception {
        itemReader.close();
    }

    @Test
    public void readExcelFile() throws Exception {
        assertEquals(3, itemReader.getNumberOfSheets());
        String[] row;
        do {
            row = (String[]) itemReader.read();
            logger.debug("Read: " + StringUtils.arrayToCommaDelimitedString(row));
            if (row != null) {
                assertEquals(6, row.length);
            }
        } while (row != null);
        int readCount = (Integer) ReflectionTestUtils.getField(itemReader, "currentItemCount");
        assertEquals(4321, readCount);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRequiredProperties() throws Exception {
        final AbstractExcelItemReader reader = createExcelItemReader();
        reader.afterPropertiesSet();
    }

    protected abstract AbstractExcelItemReader createExcelItemReader();

}
