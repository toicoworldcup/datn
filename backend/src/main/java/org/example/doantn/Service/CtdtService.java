package org.example.doantn.Service;

import org.apache.poi.ss.usermodel.*;
import org.example.doantn.Dto.response.CthDTO;
import org.example.doantn.Entity.Batch;
import org.example.doantn.Entity.Ctdt;
import org.example.doantn.Entity.Course;
import org.example.doantn.Repository.BatchRepo;
import org.example.doantn.Repository.CtdtRepo;
import org.example.doantn.Repository.CourseRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CtdtService {

    @Autowired
    private CtdtRepo ctdtRepo;
    @Autowired
    private CourseRepo courseRepo;
    @Autowired
    private BatchRepo batchRepo;

    public List<Ctdt> getAllCtdts() {
        return ctdtRepo.findAll();
    }

    public Optional<Ctdt> getCtdtByMaCt(String maCt) {
        return ctdtRepo.findByMaCt(maCt);
    }

    public Ctdt createCtdt(Ctdt ctdt) {
        return ctdtRepo.save(ctdt);
    }

    public List<CthDTO> getCoursesByMaCtAndKhoa(String maCt, String khoa) {
        Optional<Batch> batchOptional = batchRepo.findByName(khoa);

        if (batchOptional.isPresent()) {
            Batch batch = batchOptional.get();
            Optional<Ctdt> ctdtOptional = ctdtRepo.findByMaCtAndBatch(maCt, batch); // Sử dụng findByMaCtAndBatch

            if (ctdtOptional.isPresent()) {
                Ctdt ctdt = ctdtOptional.get();
                return ctdt.getCourses().stream()
                        .map(course -> new CthDTO(ctdt.getName(), course.getMaHocPhan(), course.getName(), course.getTinChi(), null, null))
                        .collect(Collectors.toList());
            } else {
                return java.util.Collections.emptyList(); // CTĐT không tồn tại với mã và khóa này
            }
        }
        return java.util.Collections.emptyList(); // Khóa không tồn tại
    }

    @Transactional
    public void assignCourses(String maCt, String khoa, List<String> maHocPhanList) {
        Optional<Batch> batchOptional = batchRepo.findByName(khoa);

        if (batchOptional.isPresent()) {
            Batch batch = batchOptional.get();
            Optional<Ctdt> ctdtOptional = ctdtRepo.findByMaCtAndBatch(maCt, batch);

            if (ctdtOptional.isPresent()) {
                Ctdt ctdt = ctdtOptional.get();
                Set<Course> courses = ctdt.getCourses();
                for (String maHocPhan : maHocPhanList) {
                    Optional<Course> courseOptional = courseRepo.findByMaHocPhan(maHocPhan);
                    courseOptional.ifPresent(courses::add);
                }
                ctdtRepo.save(ctdt);
            } else {
                throw new RuntimeException("Không tìm thấy CTĐT có mã: " + maCt + " thuộc khóa: " + khoa);
            }
        } else {
            throw new RuntimeException("Không tìm thấy khóa: " + khoa);
        }
    }

    public List<String> readMaHocPhanFromExcel(MultipartFile file) throws IOException {
        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        List<String> maHocPhanList = new ArrayList<>();
        for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row != null) {
                Cell maHpCell = row.getCell(0);
                if (maHpCell != null && maHpCell.getCellType() == CellType.STRING) {
                    String maHocPhan = maHpCell.getStringCellValue().trim();
                    if (!maHocPhan.isEmpty()) {
                        maHocPhanList.add(maHocPhan);
                    }
                }
            }
        }
        return maHocPhanList;
    }
}