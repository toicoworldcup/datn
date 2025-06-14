package org.example.doantn.Service.Import;

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
public class TeacherImport {

    @Autowired
    private TeacherRepo teacherRepo;

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

    public void importTeachersFromExcel(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;

                Teacher teacher = new Teacher();
                teacher.setName(getCellValue(row.getCell(0))); // name
                teacher.setMaGv(getCellValue(row.getCell(1))); // ma_gv
                teacher.setEmail(getCellValue(row.getCell(2))); // email
                teacher.setPhoneNumber(getCellValue(row.getCell(3))); // phoneNumber
                teacher.setCccd(getCellValue(row.getCell(4))); // cccd
                teacher.setGender(getCellValue(row.getCell(5))); // gender
                teacher.setDateOfBirth(parseDateCell(row.getCell(6))); // dob
                teacher.setAddress(getCellValue(row.getCell(7))); // address

                // Department
                String departName = getCellValue(row.getCell(8));
                if (departName != null) {
                    Optional<Department> deptOptional = departmentRepo.findByName(departName);
                    if (deptOptional.isPresent()) {
                        teacher.setDepartment(deptOptional.get());
                    } else {
                        throw new IllegalArgumentException("Department name '" + departName + "' không tồn tại.");
                    }
                }

                String rawPassword = generateRandomPassword();
                String encodedPassword = passwordEncoder.encode(rawPassword);

                RoleType teacherRole = roleRepo.findByName("TEACHER")
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy ROLE_TEACHER"));
                User user = new User(teacher.getMaGv(), encodedPassword, teacherRole);
                userRepo.save(user);
                teacher.setUser(user);
                teacherRepo.save(teacher);
                emailService.sendCredentials(teacher.getEmail(), teacher.getMaGv(), rawPassword);
            }

            System.out.println("Import giáo viên và tạo tài khoản thành công!");

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