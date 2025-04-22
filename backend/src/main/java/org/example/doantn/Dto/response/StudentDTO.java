package org.example.doantn.Dto.response;

import org.example.doantn.Entity.Student;

import java.time.LocalDate;

public class StudentDTO {
    private String name;
    private String mssv;
    private LocalDate dateOfBirth;
    private String gender;
    private String email;
    private String phone;
    private String cccd;
    private String departmentName;
    private String batchName;
    private String address;
    private String tenCth;
    public StudentDTO(Student student) {
        this.name = student.getName();
        this.mssv = student.getMssv();
        this.dateOfBirth = student.getDateOfBirth();
        this.gender = student.getGender();
        this.email = student.getEmail();
        this.phone = student.getPhone();
        this.cccd = student.getCccd();
        this.departmentName = student.getDepartment().getName();
        this.batchName = student.getBatch().getName();
        this.address = student.getAddress();
        this.tenCth=student.getCtdt().getName();


    }

    public StudentDTO() {
    }

    public StudentDTO(String address, String batchName, String cccd, LocalDate dateOfBirth, String departmentName, String email, String gender, String mssv, String name, String phone,String tenCth) {
        this.address = address;
        this.batchName = batchName;
        this.cccd = cccd;
        this.dateOfBirth = dateOfBirth;
        this.departmentName = departmentName;
        this.email = email;
        this.gender = gender;
        this.mssv = mssv;
        this.name = name;
        this.phone = phone;
        this.tenCth=tenCth;
    }

    public String getTenCth() {
        return tenCth;
    }

    public void setTenCth(String tenCth) {
        this.tenCth = tenCth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBatchName() {
        return batchName;
    }

    public void setBatchName(String batchName) {
        this.batchName = batchName;
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

    public String getMssv() {
        return mssv;
    }

    public void setMssv(String mssv) {
        this.mssv = mssv;
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
