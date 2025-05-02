package org.example.doantn.Entity;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "batch")
public class Batch  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "name")
    private String name;
    @OneToMany(mappedBy = "batch", cascade = CascadeType.ALL)
    private Set<Student> students = new HashSet<>();
    @OneToMany(mappedBy = "batch", cascade = CascadeType.ALL)
    private Set<Ctdt> ctdts = new HashSet<>();

    public Batch() {
    }

    public Set<Ctdt> getCtdts() {
        return ctdts;
    }

    public void setCtdts(Set<Ctdt> ctdts) {
        this.ctdts = ctdts;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Student> getStudents() {
        return students;
    }

    public void setStudents(Set<Student> students) {
        this.students = students;
    }
}
