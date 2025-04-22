package org.example.doantn.Controller;
import org.example.doantn.Entity.Semester;
import org.example.doantn.Service.SemesterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/semesters")
public class SemesterController {

    @Autowired
    private SemesterService semesterService;

    //@PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<Semester>> getAllSemesters() {
        return ResponseEntity.ok(semesterService.getAllSemesters());
    }

    //@PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{name}")
    public ResponseEntity<Semester> getSemesterByName(@PathVariable String name) {
        return ResponseEntity.ok(semesterService.getSemesterByName(name));
    }

    //@PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Semester> createSemester(@RequestBody Semester semester) {
        return ResponseEntity.ok(semesterService.createSemester(semester));
    }

   // @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Semester> updateSemester(@PathVariable String name, @RequestBody Semester updatedSemester) {
        return ResponseEntity.ok(semesterService.updateSemester(name, updatedSemester));
    }

   // @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{name}/open")
    public ResponseEntity<Semester> openRegistration(@PathVariable String name) {
        return ResponseEntity.ok(semesterService.updateSemesterStatus(name, true));
    }

    //@PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{name}/close")
    public ResponseEntity<Semester> closeRegistration(@PathVariable String name) {
        return ResponseEntity.ok(semesterService.updateSemesterStatus(name, false));
    }
}
