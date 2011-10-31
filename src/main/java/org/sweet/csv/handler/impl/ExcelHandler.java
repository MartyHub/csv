package org.sweet.csv.handler.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.ClassUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.sweet.csv.handler.HandlerException;
import org.sweet.csv.mapper.MappedBean;
import org.sweet.csv.mapper.MappedProperty;
import org.sweet.csv.mapper.MapperException;
import org.sweet.date.BusinessDate;

public class ExcelHandler<T> extends AbstractHandler<T> {

    private CellStyle dateCellStyle;

    private CellStyle businessDateCellStyle;

    public ExcelHandler(MappedBean<T> mappedBean) {
        super(mappedBean);
    }

    @Override
    protected void doParse(InputStream is, List<T> result) throws IOException {
        Workbook wb = new HSSFWorkbook(is);
        final int numberOfSheets = wb.getNumberOfSheets();

        if (numberOfSheets == 0) {
            return;
        }

        Sheet sheet = wb.getSheetAt(0);
        boolean headerRead = false;
        MappedBean<T> mappedBean = this.mappedBean;

        for (Iterator<Row> rit = sheet.rowIterator(); rit.hasNext();) {
            final Row row = rit.next();

            if (hasHeader && !headerRead) {
                final List<String> headers = new LinkedList<String>();

                for (Iterator<Cell> cit = row.cellIterator(); cit.hasNext();) {
                    final Cell cell = cit.next();

                    headers.add(cell.getStringCellValue());
                }

                headerRead = true;

                mappedBean = mappedBean.withHeaders(headers.toArray(new String[headers.size()]));
            } else {
                if (!isBlank(row)) {
                    final T bean = mappedBean.newInstance();
                    Iterator<MappedProperty<T>> pit = mappedBean.iterator();

                    for (int i = 0, size = row.getLastCellNum(); i < size && pit.hasNext(); ++i) {
                        final Cell cell = row.getCell(i, Row.RETURN_BLANK_AS_NULL);

                        if (cell == null) {
                            pit.next();
                        } else {
                            final MappedProperty<T> mappedProperty = pit.next();
                            final Object value = getCellValue(cell, mappedProperty);

                            if (value != null) {
                                mappedProperty.setValue(bean, value);
                            }
                        }
                    }

                    result.add(bean);
                }
            }
        }
    }

    public void write(Iterable<T> iterable, OutputStream out) {
        Workbook wb = new HSSFWorkbook();
        Sheet sheet = wb.createSheet();
        int rowIndex = 0;
        int nbColumns = 0;

        if (hasHeader) {
            Row row = sheet.createRow(rowIndex);
            int columnIndex = 0;

            for (String header : mappedBean.getHeaders()) {
                row.createCell(columnIndex).setCellValue(header);

                ++columnIndex;
            }

            nbColumns = columnIndex;

            ++rowIndex;
        }

        dateCellStyle = wb.createCellStyle();
        dateCellStyle.setDataFormat(wb.getCreationHelper().createDataFormat().getFormat("yyyy-mm-dd hh:mm:ss"));

        businessDateCellStyle = wb.createCellStyle();
        businessDateCellStyle.setDataFormat(wb.getCreationHelper().createDataFormat().getFormat("yyyy-mm-dd"));

        for (T bean : iterable) {
            if (bean != null) {
                Row row = sheet.createRow(rowIndex);
                int columnIndex = 0;

                for (MappedProperty<T> mappedProperty : mappedBean) {
                    final Object value = mappedProperty.getValue(bean);
                    final Cell cell = row.createCell(columnIndex);

                    setCellValue(cell, value);

                    ++columnIndex;
                }

                nbColumns = Math.max(columnIndex, nbColumns);

                ++rowIndex;
            }
        }

        for (int i = 0; i < nbColumns; ++i) {
            sheet.autoSizeColumn(i);
        }

        try {
            wb.write(out);
        } catch (IOException ioe) {
            throw new HandlerException("Failed to write Excel stream", ioe);
        }
    }

    private void setCellValue(Cell cell, Object value) {
        if (value == null) {
            cell.setCellType(Cell.CELL_TYPE_BLANK);

            return;
        }

        Class<? extends Object> type = value.getClass();

        if (String.class.equals(type)) {
            cell.setCellValue((String) value);
        } else if (Number.class.equals(type)) {
            cell.setCellValue(((Number) value).doubleValue());
        } else if (Boolean.class.equals(type) || Boolean.TYPE.equals(type)) {
            cell.setCellValue(((Boolean) value).booleanValue());
        } else if (Date.class.equals(type)) {
            cell.setCellStyle(dateCellStyle);
            cell.setCellValue((Date) value);
        } else if (BusinessDate.class.equals(type)) {
            cell.setCellStyle(businessDateCellStyle);
            cell.setCellValue(((BusinessDate) value).asDate());
        } else {
            cell.setCellValue(String.valueOf(value));
        }
    }

    private Object getCellValue(Cell cell, MappedProperty<T> mappedProperty) {
        if (Cell.CELL_TYPE_BLANK == cell.getCellType()) {
            return null;
        }

        Class<?> type = mappedProperty.getType();

        try {
            if (String.class.equals(type)) {
                return cell.getStringCellValue();
            } else if (Double.class.equals(type)) {
                return cell.getNumericCellValue();
            } else if (Integer.class.equals(type)) {
                return Integer.valueOf((int) cell.getNumericCellValue());
            } else if (Long.class.equals(type)) {
                return Long.valueOf((long) cell.getNumericCellValue());
            } else if (Boolean.class.equals(type) || Boolean.TYPE.equals(type)) {
                return cell.getBooleanCellValue();
            } else if (Date.class.equals(type)) {
                return cell.getDateCellValue();
            } else if (BusinessDate.class.equals(type)) {
                return new BusinessDate(cell.getDateCellValue());
            }
        } catch (IllegalStateException e) {
            throw new MapperException("Invalid cell value for <" + mappedProperty.getHeader() + "> on line " + cell.getRowIndex() + " (expected <"
                    + ClassUtils.getShortClassName(type) + ">)");
        }

        throw new MapperException("Don't know how to get <" + type + "> from Excel");
    }

    private boolean isBlank(Row row) {
        for (int i = 0, size = row.getLastCellNum(); i < size; ++i) {
            final Cell cell = row.getCell(i, Row.RETURN_BLANK_AS_NULL);

            if (cell != null) {
                return false;
            }
        }

        return true;
    }
}
