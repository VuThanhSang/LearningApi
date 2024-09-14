package com.example.learning_api.service.common;
import com.example.learning_api.dto.response.deadline.DeadlineStatistics;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ExcelExportService {

    public ByteArrayInputStream exportDeadlineStatisticsToExcel(List<DeadlineStatistics> statistics) throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Deadline Statistics");

            // Create header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Deadline ID");
            headerRow.createCell(1).setCellValue("Title");
            headerRow.createCell(2).setCellValue("Description");
            headerRow.createCell(3).setCellValue("Start Date");
            headerRow.createCell(4).setCellValue("End Date");
            headerRow.createCell(5).setCellValue("Student ID");
            headerRow.createCell(6).setCellValue("Student Name");
            headerRow.createCell(7).setCellValue("Grade");
            headerRow.createCell(8).setCellValue("Status");

            int rowIdx = 1;
            for (DeadlineStatistics stat : statistics) {
                for (DeadlineStatistics.StudentSubmission submission : stat.getStudents()) {
                    Row row = sheet.createRow(rowIdx++);
                    row.createCell(0).setCellValue(stat.get_id());
                    row.createCell(1).setCellValue(stat.getTitle());
                    row.createCell(2).setCellValue(stat.getDescription());
                    row.createCell(3).setCellValue(stat.getStartDate().toString());
                    row.createCell(4).setCellValue(stat.getEndDate().toString());
                    row.createCell(5).setCellValue(submission.getStudentId());
                    row.createCell(6).setCellValue(submission.getStudentName());
                    row.createCell(7).setCellValue(submission.getGrade());
                    row.createCell(8).setCellValue(submission.getStatus());
                }
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }
}