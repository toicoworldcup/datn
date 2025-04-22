package org.example.doantn.Entity;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity

public class Ctdt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name="Ten chuong trinh")
    private String name;
    @Column(name="Ma chuong trinh")
    private String maCt;
    @OneToMany(mappedBy = "ctdt")
    private List<Student> students;
    @ManyToMany
    @JoinTable(
            name = "course_ctdt",
            joinColumns = @JoinColumn(name = "ctdt_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private Set<Course> courses = new HashSet<>();

    public Ctdt() {
    }

    public Ctdt(Set<Course> courses, String name, List<Student> students) {
        this.courses = courses;
        this.name = name;
        this.students = students;
    }

    public Set<Course> getCourses() {
        return courses;
    }

    public void setCourses(Set<Course> courses) {
        this.courses = courses;
    }

    public String getMaCt() {
        return maCt;
    }

    public void setMaCt(String maCt) {
        this.maCt = maCt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
