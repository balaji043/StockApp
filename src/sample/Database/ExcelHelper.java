package sample.Database;

import javafx.collections.ObservableList;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jetbrains.annotations.NotNull;
import sample.Alert.AlertMaker;
import sample.Utils.Preferences;
import sample.model.Log;
import sample.model.Product;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import static sample.Database.DatabaseHelper_Product.getNeededProductList;
import static sample.Database.DatabaseHelper_Product.getProductList;

public class ExcelHelper {
    // get products from excel
    private static boolean okay;
    private static File dest;

    private static ArrayList<Product> getProducts(@NotNull Sheet sheet) {
        ArrayList<Product> products = new ArrayList<>();
        String c = "", s = "", n = "", pa = "", pl = "", h = "", m = "0", q = "0", r = "", min = "0";
        try {
            int i = 0;
            for (Row currentRow : sheet) {
                if (i == 0) {
                    i++;
                    continue;
                }
                if (currentRow.getCell(0) != null) {
                    currentRow.getCell(0).setCellType(CellType.STRING);
                    c = currentRow.getCell(0).getStringCellValue();
                }
                if (currentRow.getCell(1) != null) {
                    currentRow.getCell(1).setCellType(CellType.STRING);
                    s = currentRow.getCell(1).getStringCellValue();
                }
                if (currentRow.getCell(2) != null) {
                    currentRow.getCell(2).setCellType(CellType.STRING);
                    n = currentRow.getCell(2).getStringCellValue();
                }
                if (currentRow.getCell(3) != null) {
                    currentRow.getCell(3).setCellType(CellType.STRING);
                    pa = currentRow.getCell(3).getStringCellValue();
                }
                if (currentRow.getCell(4) != null) {
                    if (currentRow.getCell(4).getCellType() == CellType.STRING)
                        q = "" + currentRow.getCell(4).getStringCellValue();
                    if (currentRow.getCell(4).getCellType() == CellType.NUMERIC)
                        q = "" + (int) currentRow.getCell(4).getNumericCellValue();
                }
                if (currentRow.getCell(5) != null) {
                    if (currentRow.getCell(5).getCellType() == CellType.STRING)
                        m = "" + currentRow.getCell(5).getStringCellValue();
                    if (currentRow.getCell(5).getCellType() == CellType.NUMERIC)
                        m = "" + (int) currentRow.getCell(5).getNumericCellValue();
                }
                if (currentRow.getCell(6) != null) {
                    if (currentRow.getCell(6).getCellType() == CellType.STRING)
                        h = "" + currentRow.getCell(6).getStringCellValue();
                    if (currentRow.getCell(6).getCellType() == CellType.NUMERIC)
                        h = "" + (int) currentRow.getCell(6).getNumericCellValue();
                }
                if (currentRow.getCell(7) != null) {
                    currentRow.getCell(7).setCellType(CellType.STRING);
                    pl = currentRow.getCell(7).getStringCellValue();
                }
                if (currentRow.getCell(8) != null) {
                    currentRow.getCell(8).setCellType(CellType.STRING);
                    r = currentRow.getCell(8).getStringCellValue();
                }
                if (currentRow.getCell(9) != null) {
                    currentRow.getCell(9).setCellType(CellType.STRING);
                    min = currentRow.getCell(9).getStringCellValue();
                }
                if (currentRow.getCell(0).getStringCellValue().isEmpty() ||
                        currentRow.getCell(1).getStringCellValue().isEmpty() ||
                        currentRow.getCell(2).getStringCellValue().isEmpty()) {
                    continue;
                }

                Product p = new Product(c, s, n, pa, q, m, h, pl, r, sheet.getSheetName());
                p.setMin(min);
                products.add(p);
            }
            return products;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return products;
    }

    // EXCEL TO SQLITE
    // PRODUCTS FROM EXCEL TO SQLITE
    public static boolean excelTOSQLite() {
        okay = false;
        String fileName = Preferences.getPreferences()
                .getPath();
        FileInputStream excel;
        Workbook workbook;
        try {
            excel = new FileInputStream(new File(fileName));
            workbook = new XSSFWorkbook(excel);
            int n = workbook.getNumberOfSheets();
            ArrayList<Product> products;
            Sheet sheet;
            for (int i = 0; i < n; i++) {
                sheet = workbook.getSheetAt(i);
                String name = sheet.getSheetName();
                products = getProducts(sheet);
                DatabaseHelper.createProductTable(name);
                for (Product pa : products) {
                    if (!DatabaseHelper_Product.isProductExist(pa, name))
                        DatabaseHelper_Product.insertNewProduct(pa, name);
                    else
                        DatabaseHelper_Product.updateProduct(pa, name);
                }
            }
            okay = true;
        } catch (Exception e) {
            okay = false;
            e.printStackTrace();
        }
        return okay;
    }

    // SQLITE to Excel
    public static boolean productSQLToExcel(@NotNull File dest) {
        okay = false;
        ObservableList<Product> products;
        Set<String> tableNames =
                Preferences.getPreferences().getTableNames();
        FileInputStream excel = null;
        XSSFWorkbook workbook = null;
        try {
            String FILE_NAME = dest.getAbsolutePath();
            XSSFSheet sheet;

            try {
                excel = new FileInputStream(dest);
                workbook = new XSSFWorkbook(excel);
            } catch (Exception e) {
                workbook = new XSSFWorkbook();
            }

            for (String s : tableNames) {
                try {
                    sheet = workbook.createSheet(s);
                } catch (Exception e) {
                    sheet = workbook.getSheet(s);
                }
                products = getProductList(s);
                int rowNum = 0;
                Row row;
                row = sheet.createRow(rowNum);
                rowNum++;
                row.createCell(0).setCellValue("Category");
                row.createCell(1).setCellValue("SubCategory");
                row.createCell(2).setCellValue("Name");
                row.createCell(3).setCellValue("PartNo");
                row.createCell(4).setCellValue("QTY");
                row.createCell(5).setCellValue("MRP");
                row.createCell(6).setCellValue("HSNCode");
                row.createCell(7).setCellValue("Place");
                row.createCell(8).setCellValue("Remarks");
                row.createCell(9).setCellValue("Minimum");

                for (Product p : products) {
                    row = sheet.createRow(rowNum);
                    rowNum++;
                    row.createCell(0).setCellValue(p.getCategory());
                    row.createCell(1).setCellValue(p.getSubCategory());
                    row.createCell(2).setCellValue(p.getName());
                    row.createCell(3).setCellValue(p.getPartNo());
                    row.createCell(4).setCellValue(p.getQTY());
                    row.createCell(5).setCellValue(p.getMRP());
                    row.createCell(6).setCellValue(p.getHsnCode());
                    row.createCell(7).setCellValue(p.getPlace());
                    row.createCell(8).setCellValue(p.getRemarks());
                    row.createCell(9).setCellValue(p.getMin());

                }
            }
            FileOutputStream outputStream = new FileOutputStream(FILE_NAME);
            workbook.write(outputStream);
            okay = true;
        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
            e.printStackTrace();
        } finally {
            if (excel != null) {
                try {
                    excel.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            try {
                assert workbook != null;
                workbook.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return okay;
    }

    public static boolean needProductSQLToExcel(@NotNull File dest, @NotNull Set<String> tableNames) {
        okay = false;
        FileInputStream excel = null;
        XSSFWorkbook workbook = null;

        ObservableList<Product> products;
        try {
            String FILE_NAME = dest.getAbsolutePath();
            XSSFSheet sheet;

            try {
                excel = new FileInputStream(dest);
                workbook = new XSSFWorkbook(excel);
            } catch (Exception e) {
                workbook = new XSSFWorkbook();
            }

            for (String s : tableNames) {
                products = getNeededProductList(s);
                if (products.size() == 0) continue;
                try {
                    sheet = workbook.createSheet(s);
                } catch (Exception e) {
                    sheet = workbook.getSheet(s);
                }
                int rowNum = 0;
                Row row;
                row = sheet.createRow(rowNum);
                rowNum++;
                row.createCell(0).setCellValue("Brand");
                row.createCell(1).setCellValue("Name");
                row.createCell(2).setCellValue("QTY");
                row.createCell(3).setCellValue("Min QTY");
                row.createCell(4).setCellValue("Part NO");
                for (Product p : products) {
                    row = sheet.createRow(rowNum);
                    rowNum++;
                    row.createCell(0).setCellValue(s);
                    row.createCell(1).setCellValue(p.getName());
                    row.createCell(2).setCellValue(p.getQTY());
                    row.createCell(3).setCellValue(p.getMin());
                    row.createCell(4).setCellValue(p.getPartNo());
                }
            }

            FileOutputStream outputStream = new FileOutputStream(FILE_NAME);
            workbook.write(outputStream);
            okay = true;
        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
            e.printStackTrace();
        } finally {
            assert excel != null;
            try {
                excel.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            assert workbook != null;
            try {
                workbook.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return okay;
    }

    public static boolean allLogsFromSqlToExcel(@NotNull File dest, @NotNull ObservableList<Log> logs) {
        okay = false;
        try {
            String FILE_NAME = dest.getAbsolutePath();
            FileInputStream excel;
            XSSFWorkbook workbook;
            XSSFSheet sheet;

            try {
                excel = new FileInputStream(dest);
                workbook = new XSSFWorkbook(excel);
            } catch (Exception e) {
                workbook = new XSSFWorkbook();
            }
            try {
                sheet = workbook.createSheet("History");
            } catch (Exception e) {
                sheet = workbook.getSheet("History");
            }
            int rowNum = 0;
            Row row = sheet.createRow(rowNum);
            row.createCell(0).setCellValue("Brand");
            row.createCell(1).setCellValue("Name");
            row.createCell(2).setCellValue("Date");
            row.createCell(3).setCellValue("Entry");
            row.createCell(4).setCellValue("Received");
            row.createCell(5).setCellValue("Issued");
            row.createCell(6).setCellValue("Balance");
            row.createCell(7).setCellValue("Action");
            rowNum++;
            for (Log l : logs) {
                row = sheet.createRow(rowNum);
                row.createCell(0).setCellValue(l.getBrand());
                row.createCell(1).setCellValue(l.getName());
                row.createCell(2).setCellValue(l.getDate());
                row.createCell(3).setCellValue(l.getEntry());
                row.createCell(4).setCellValue(l.getReceived());
                row.createCell(5).setCellValue(l.getIssued());
                row.createCell(6).setCellValue(l.getBalance());
                row.createCell(7).setCellValue(l.getAction());

                rowNum++;
            }
            FileOutputStream outputStream = new FileOutputStream(FILE_NAME);
            workbook.write(outputStream);
            outputStream.close();
            workbook.close();
            okay = true;
        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
        }
        return okay;
    }

}
