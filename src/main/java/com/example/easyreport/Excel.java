package com.example.easyreport;

import lombok.Builder;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Excel {

    private final String localDatePattern;
    private final String localDateTimePattern;
    private final IndexedColors headerColor;

    @Builder
    public Excel(String localDatePattern, String localDateTimePattern, IndexedColors headerColor) {
        this.localDatePattern = localDatePattern == null ? "yyyy-MM-dd" : localDatePattern;
        this.localDateTimePattern = localDateTimePattern == null ? "YYYY-MM-dd HH:mm" : localDateTimePattern;
        this.headerColor = headerColor == null ? IndexedColors.WHITE : headerColor;
    }

    public void write(OutputStream output, List<?> data) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, IOException {
        Map<Integer, List<?>> file = extractData(data);

        try(Workbook workbook = new XSSFWorkbook()) {
            int rowNum = 0;
            int numberColumns = 0;
            Sheet sheet = workbook.createSheet();
            DateTimeFormatter localDateFormatter = DateTimeFormatter.ofPattern(localDatePattern);
            DateTimeFormatter localDateTimeFormatter = DateTimeFormatter.ofPattern(localDateTimePattern);
            
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(headerColor.index);
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);

            CellStyle bodyStyle = workbook.createCellStyle();
            bodyStyle.setBorderTop(BorderStyle.THIN);
            bodyStyle.setBorderRight(BorderStyle.THIN);
            bodyStyle.setBorderBottom(BorderStyle.THIN);
            bodyStyle.setBorderLeft(BorderStyle.THIN);

            for(List<?> line : file.values()) {
                numberColumns = line.size();
                Row row = sheet.createRow(rowNum++);
                int cellNum = 0;

                for(Object item : line) {
                    Cell cell = row.createCell(cellNum++);
                    cell.setCellStyle(bodyStyle);

                    if(rowNum == 1) {
                        cell.setCellStyle(headerStyle);
                    }

                    if(item instanceof String v) {
                        cell.setCellValue(v);
                    } else if(item instanceof Integer v) {
                        cell.setCellValue(v);
                    } else if(item instanceof LocalDate v) {
                        cell.setCellValue(v.format(localDateFormatter));
                    } else if(item instanceof LocalDateTime v) {
                        cell.setCellValue(v.format(localDateTimeFormatter));
                    }
                }
            }

            autoSizeColumns(sheet, numberColumns);
            workbook.write(output);
        }
    }

    private Map<Integer, List<?>> extractData(List<?> data) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        List<String> headers = Arrays.stream(data.get(0).getClass().getDeclaredFields())
                .map(f -> f.getAnnotation(ReportColumn.class).title())
                .toList();

        int row = 0;
        Map<Integer, List<?>> file = new TreeMap<>();
        file.put(row++, headers);

        for(Object obj : data) {
            Field[] fields = obj.getClass().getDeclaredFields();
            List<Object> line = new ArrayList<>();

            for (Field field : fields) {
                if (field.isAnnotationPresent(ReportColumn.class)) {
                    String methodName = "get" + capitalize(field.getName());
                    Method method = obj.getClass().getDeclaredMethod(methodName);
                    Object objValue = method.invoke(obj);
                    line.add(objValue);
                }
            }

            file.put(row++, line);
        }

        return file;
    }

    private String capitalize(String str) {
        if (str == null) {
            return null;
        }
        return Character.toTitleCase(str.charAt(0)) +
                str.substring(1);
    }

    private void autoSizeColumns(Sheet sheet, int numberColumns) {
        for (int i = 0; i < numberColumns; i++) {
            sheet.autoSizeColumn(i + 1);
        }
    }

}
