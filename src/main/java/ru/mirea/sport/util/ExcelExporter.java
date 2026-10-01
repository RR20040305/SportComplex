package ru.mirea.sport.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import ru.mirea.sport.model.Booking;
import ru.mirea.sport.model.Client;
import ru.mirea.sport.model.Facility;

public class ExcelExporter {
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public static void exportBookings(List<Booking> bookings, String path) throws IOException {
        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Брони");

            String[] headers = {
                "ID", "Клиент", "Телефон", "Площадка", "Вид спорта",
                "Цена/час", "Начало", "Окончание", "Статус", "Создана"
            };
            writeHeader(sheet, headers);

            int rowNum = 1;
            for (Booking b : bookings) {
                Row r = sheet.createRow(rowNum++);
                r.createCell(0).setCellValue(b.getId());
                r.createCell(1).setCellValue(b.getClient().getFullName());
                r.createCell(2).setCellValue(b.getClient().getPhone());
                r.createCell(3).setCellValue(b.getFacility().getName());
                r.createCell(4).setCellValue(b.getFacility().getSportType());
                r.createCell(5).setCellValue(b.getFacility().getPricePerHour().doubleValue());
                r.createCell(6).setCellValue(b.getStartTime().format(DATETIME_FMT));
                r.createCell(7).setCellValue(b.getEndTime().format(DATETIME_FMT));
                r.createCell(8).setCellValue(b.getStatus().getDisplayName());
                r.createCell(9).setCellValue(b.getCreatedAt() == null ? "" : b.getCreatedAt().format(DATETIME_FMT));
            }

            autoSize(sheet, headers.length);
            try (FileOutputStream out = new FileOutputStream(path)) {
                wb.write(out);
            }
        }
    }

    public static void exportClients(List<Client> clients, String path) throws IOException {
        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Клиенты");

            String[] headers = {"ID", "ФИО", "Телефон", "Email", "Создан"};
            writeHeader(sheet, headers);

            int rowNum = 1;
            for (Client c : clients) {
                Row r = sheet.createRow(rowNum++);
                r.createCell(0).setCellValue(c.getId());
                r.createCell(1).setCellValue(c.getFullName());
                r.createCell(2).setCellValue(c.getPhone());
                r.createCell(3).setCellValue(c.getEmail() == null ? "" : c.getEmail());
                r.createCell(4).setCellValue(c.getCreatedAt() == null ? "" : c.getCreatedAt().format(DATETIME_FMT));
            }

            autoSize(sheet, headers.length);
            try (FileOutputStream out = new FileOutputStream(path)) {
                wb.write(out);
            }
        }
    }

    public static void exportFacilities(List<Facility> facilities, String path) throws IOException {
        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Площадки");

            String[] headers = {"ID", "Название", "Вид спорта", "Цена/час"};
            writeHeader(sheet, headers);

            int rowNum = 1;
            for (Facility f : facilities) {
                Row r = sheet.createRow(rowNum++);
                r.createCell(0).setCellValue(f.getId());
                r.createCell(1).setCellValue(f.getName());
                r.createCell(2).setCellValue(f.getSportType());
                r.createCell(3).setCellValue(f.getPricePerHour().doubleValue());
            }

            autoSize(sheet, headers.length);
            try (FileOutputStream out = new FileOutputStream(path)) {
                wb.write(out);
            }
        }
    }

    private static void writeHeader(Sheet sheet, String[] headers) {
        CellStyle style = sheet.getWorkbook().createCellStyle();
        Font font = sheet.getWorkbook().createFont();
        font.setBold(true);
        style.setFont(font);

        Row header = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(style);
        }
    }

    private static void autoSize(Sheet sheet, int columns) {
        for (int i = 0; i < columns; i++) {
            sheet.autoSizeColumn(i);
            int width = sheet.getColumnWidth(i);
            sheet.setColumnWidth(i, Math.min(width + 1024, 255 * 256));
        }
    }
}