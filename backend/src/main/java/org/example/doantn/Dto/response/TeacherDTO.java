package org.example.doantn.Dto.response;

import org.example.doantn.Entity.Teacher;

import java.time.LocalDate;

public class TeacherDTO {
    private String name;
    private String maGv;
    private LocalDate dateOfBirth;
    private String gender;
    private String email;
    private String phone;
    private String cccd;
    private String departmentName;
    private String address;
    public TeacherDTO(Teacher teacher) {
        this.name = teacher.getName();
        this.maGv = teacher.getMaGv();
        this.dateOfBirth = teacher.getDateOfBirth();
        this.gender = teacher.getGender();
        this.email = teacher.getEmail();
        this.phone = teacher.getPhoneNumber();
        this.cccd = teacher.getCccd();
        this.departmentName = teacher.getDepartment().getName();
        this.address = teacher.getAddress();

    }

    public TeacherDTO() {
    }

    public TeacherDTO(String address, String cccd, LocalDate dateOfBirth, String departmentName, String email, String gender, String maGv, String name, String phone) {
        this.address = address;
        this.cccd = cccd;
        this.dateOfBirth = dateOfBirth;
        this.departmentName = departmentName;
        this.email = email;
        this.gender = gender;
        this.maGv = maGv;
        this.name = name;
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public String getCccd() {
        return cccd;
    }

    public void setCccd(String cccd) {
        this.cccd = cccd;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMaGv() {
        return maGv;
    }

    public void setMaGv(String maGv) {
        this.maGv = maGv;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
