package org.example.doantn.Export;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.doantn.Entity.Student;
import org.example.doantn.Repository.StudentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Service
public class StudentExport {
    @Autowired
    private StudentRepo studentRepo;

    public void exportStudentsToExcel(HttpServletResponse response) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Students");

            // Tạo header (tiêu đề cột)
            Row headerRow = sheet.createRow(0);
            String[] headers = {"tên", "mssv", "Date of Birth", "giới tính", "email", "sdt", "cccd", "trường", "khoá","đia chỉ","chương trình đào tạo"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(createHeaderCellStyle(workbook));
            }

            // Lấy danh sách sinh viên từ database
            List<Student> students = studentRepo.findAll();
            int rowNum = 1;
            for (Student student : students) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(student.getName());
                row.createCell(1).setCellValue(student.getMssv());
                row.createCell(2).setCellValue(student.getDateOfBirth() != null ? student.getDateOfBirth().toString() : "");
                row.createCell(3).setCellValue(student.getGender());
                row.createCell(4).setCellValue(student.getEmail() );
                row.createCell(5).setCellValue(student.getPhone());
                row.createCell(6).setCellValue(student.getCccd());
                row.createCell(7).setCellValue(student.getDepartment().getName());
                row.createCell(8).setCellValue(student.getBatch().getName());
                row.createCell(9).setCellValue(student.getAddress());
                row.createCell(10).setCellValue(student.getCtdt() != null && student.getCtdt().getName() != null ? student.getCtdt().getName() : "");





            }

            // Xuất file về client
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=students.xlsx");

            ServletOutputStream outputStream = response.getOutputStream();
            workbook.write(outputStream);
            outputStream.close();

            System.out.println("Export dữ liệu ra Excel thành công!");
        } catch (IOException e) {
            System.err.println("Lỗi khi xuất file Excel: " + e.getMessage());
        }
    }

    private CellStyle createHeaderCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        return style;
    }
}
