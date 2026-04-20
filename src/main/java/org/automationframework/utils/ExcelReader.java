package org.automationframework.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * Utility class for reading and writing Excel files.
 * Supports both .xls and .xlsx formats.
 */
public class ExcelReader {

    private static final Logger logger = LoggerFactory.getLogger(ExcelReader.class);

    /**
     * Reads data from Excel file and returns as list of maps.
     * First row is treated as headers.
     *
     * @param filePath Path to Excel file
     * @param sheetName Name of sheet to read
     * @return List of maps where each map represents a row
     */
    public static List<Map<String, String>> readExcel(String filePath, String sheetName) {
        List<Map<String, String>> data = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(new File(filePath));
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                logger.warn("Sheet '{}' not found in Excel file", sheetName);
                return data;
            }

            // Get headers from first row
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                logger.warn("Header row is empty in sheet: {}", sheetName);
                return data;
            }

            List<String> headers = new ArrayList<>();
            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                Cell cell = headerRow.getCell(i);
                headers.add(getCellValue(cell));
            }

            // Read data rows
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }

                Map<String, String> rowData = new LinkedHashMap<>();
                for (int j = 0; j < headers.size(); j++) {
                    Cell cell = row.getCell(j);
                    String value = getCellValue(cell);
                    rowData.put(headers.get(j), value);
                }

                // Only add row if it has at least one non-empty value
                if (rowData.values().stream().anyMatch(v -> v != null && !v.isEmpty())) {
                    data.add(rowData);
                }
            }

            logger.info("Successfully read {} rows from sheet '{}'", data.size(), sheetName);

        } catch (IOException e) {
            logger.error("Error reading Excel file: {}", filePath, e);
            throw new RuntimeException("Failed to read Excel file", e);
        }

        return data;
    }

    /**
     * Reads specific column from Excel sheet.
     *
     * @param filePath Path to Excel file
     * @param sheetName Sheet name
     * @param columnName Column header name
     * @return List of values from the column
     */
    public static List<String> readColumn(String filePath, String sheetName, String columnName) {
        List<String> columnData = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(new File(filePath));
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                logger.warn("Sheet '{}' not found in Excel file", sheetName);
                return columnData;
            }

            Row headerRow = sheet.getRow(0);
            int columnIndex = -1;

            // Find column index
            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                if (getCellValue(headerRow.getCell(i)).equals(columnName)) {
                    columnIndex = i;
                    break;
                }
            }

            if (columnIndex == -1) {
                logger.warn("Column '{}' not found in sheet", columnName);
                return columnData;
            }

            // Read column data
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    Cell cell = row.getCell(columnIndex);
                    columnData.add(getCellValue(cell));
                }
            }

            logger.info("Successfully read {} values from column '{}'", columnData.size(), columnName);

        } catch (IOException e) {
            logger.error("Error reading Excel file: {}", filePath, e);
            throw new RuntimeException("Failed to read Excel file", e);
        }

        return columnData;
    }

    /**
     * Reads specific cell value from Excel.
     *
     * @param filePath Path to Excel file
     * @param sheetName Sheet name
     * @param rowNum Row number (0-indexed)
     * @param columnNum Column number (0-indexed)
     * @return Cell value
     */
    public static String readCell(String filePath, String sheetName, int rowNum, int columnNum) {
        try (FileInputStream fis = new FileInputStream(new File(filePath));
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                logger.warn("Sheet '{}' not found in Excel file", sheetName);
                return "";
            }

            Row row = sheet.getRow(rowNum);
            if (row == null) {
                logger.warn("Row {} not found in sheet", rowNum);
                return "";
            }

            Cell cell = row.getCell(columnNum);
            return getCellValue(cell);

        } catch (IOException e) {
            logger.error("Error reading Excel file: {}", filePath, e);
            throw new RuntimeException("Failed to read Excel file", e);
        }
    }

    /**
     * Writes data to Excel file.
     *
     * @param filePath Path to Excel file
     * @param sheetName Sheet name
     * @param data List of maps representing rows
     */
    public static void writeExcel(String filePath, String sheetName, List<Map<String, String>> data) {
        try {
            Workbook workbook;
            File file = new File(filePath);

            // Create new workbook or use existing
            if (file.exists()) {
                FileInputStream fis = new FileInputStream(file);
                workbook = new XSSFWorkbook(fis);
                fis.close();
            } else {
                workbook = new XSSFWorkbook();
            }

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                sheet = workbook.createSheet(sheetName);
            }

            if (data.isEmpty()) {
                logger.warn("No data to write");
                return;
            }

            // Write headers
            Set<String> headers = data.get(0).keySet();
            Row headerRow = sheet.createRow(0);
            int colIndex = 0;
            for (String header : headers) {
                Cell cell = headerRow.createCell(colIndex++);
                cell.setCellValue(header);
            }

            // Write data
            int rowIndex = 1;
            for (Map<String, String> row : data) {
                Row excelRow = sheet.createRow(rowIndex++);
                colIndex = 0;
                for (String header : headers) {
                    Cell cell = excelRow.createCell(colIndex++);
                    cell.setCellValue(row.get(header));
                }
            }

            // Write to file
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
                logger.info("Successfully wrote {} rows to Excel file", data.size());
            }

            workbook.close();

        } catch (IOException e) {
            logger.error("Error writing Excel file: {}", filePath, e);
            throw new RuntimeException("Failed to write Excel file", e);
        }
    }

    /**
     * Gets all sheet names from Excel file.
     *
     * @param filePath Path to Excel file
     * @return List of sheet names
     */
    public static List<String> getSheetNames(String filePath) {
        List<String> sheetNames = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(new File(filePath));
             Workbook workbook = new XSSFWorkbook(fis)) {

            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                sheetNames.add(workbook.getSheetName(i));
            }

            logger.info("Found {} sheets in Excel file", sheetNames.size());

        } catch (IOException e) {
            logger.error("Error reading Excel file: {}", filePath, e);
            throw new RuntimeException("Failed to read Excel file", e);
        }

        return sheetNames;
    }

    /**
     * Gets cell value as String.
     */
    private static String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    yield cell.getDateCellValue().toString();
                } else {
                    yield String.valueOf(cell.getNumericCellValue());
                }
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            case BLANK -> "";
            default -> "";
        };
    }
}

