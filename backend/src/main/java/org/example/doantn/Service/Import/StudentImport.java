package org.example.doantn.Import;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.doantn.Entity.*;
import org.example.doantn.Repository.*;
import org.example.doantn.Service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import java.util.Random;

@Service
public class StudentImport {

    @Autowired
    private StudentRepo studentRepo;

    @Autowired
    private BatchRepo batchRepo;

    @Autowired
    private DepartmentRepo departmentRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;
    @Autowired
    private CtdtRepo ctdtRepo;

    public void importStudentsFromExcel(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;

                Student student = new Student();
                student.setName(getCellValue(row.getCell(0)));
                String mssv = getCellValue(row.getCell(1));
                String email = getCellValue(row.getCell(2));
                student.setMssv(mssv);
                student.setEmail(email);
                student.setGender(getCellValue(row.getCell(3)));
                student.setPhone(getCellValue(row.getCell(5)));
                student.setCccd(getCellValue(row.getCell(4)));
                student.setDateOfBirth(parseDateCell(row.getCell(6)));
                student.setAddress(getCellValue(row.getCell(7)));
                String batchName = getCellValue(row.getCell(8));
                if (batchName != null) {
                    Optional<Batch> batchOptional = batchRepo.findByName(batchName);
                    if (batchOptional.isPresent()) {
                        student.setBatch(batchOptional.get());
                    } else {
                        throw new IllegalArgumentException("Batch ID " + batchName + " không tồn tại.");
                    }
                }

                // Department
                String departName = getCellValue(row.getCell(9));
                if (departName != null) {
                    Optional<Department> deptOptional = departmentRepo.findByName(departName);
                    if (deptOptional.isPresent()) {
                        student.setDepartment(deptOptional.get());
                    } else {
                        throw new IllegalArgumentException("Department ID " + departName + " không tồn tại.");
                    }
                }
                String ctdt = getCellValue(row.getCell(10));
                if(ctdt != null) {
                    Optional<Ctdt> ctdtOptional = ctdtRepo.findByName(ctdt);
                    if (ctdtOptional.isPresent()) {
                        student.setCtdt(ctdtOptional.get());
                    } else {
                        throw new IllegalArgumentException("Ctdt ID " + ctdt + " không tồn tại.");
                    }
                }

                String rawPassword = generateRandomPassword();
                String encodedPassword = passwordEncoder.encode(rawPassword);

                RoleType studentRole = roleRepo.findByName("STUDENT")
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy ROLE_STUDENT"));
                User user = new User(mssv, encodedPassword, studentRole);
                userRepo.save(user);
                student.setUser(user);
                studentRepo.save(student);
                emailService.sendCredentials(email, mssv, rawPassword);
            }

            System.out.println(" Import sinh viên và tạo tài khoản thành công!");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi đọc file Excel: " + e.getMessage());
        }
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((int) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case BLANK -> "";
            default -> "";
        };
    }

    private LocalDate parseDateCell(Cell cell) {
        if (cell == null || cell.getCellType() != CellType.NUMERIC || !DateUtil.isCellDateFormatted(cell)) {
            return null;
        }
        Date date = cell.getDateCellValue();
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private Integer parseIntegerCell(Cell cell) {
        if (cell == null || cell.getCellType() != CellType.NUMERIC) return null;
        return (int) cell.getNumericCellValue();
    }

    private String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
