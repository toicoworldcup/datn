    package org.example.doantn.Entity;

    import com.fasterxml.jackson.annotation.JsonIgnore;
    import jakarta.persistence.*;

    import java.util.HashSet;
    import java.util.Set;

    @Entity
    @Table(name="course")
    public class Course {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer id;
        @Column(name = "name", length = 255)
        private String name;

        @Column(name = "ma_hoc_phan")
        private String maHocPhan;

        @Column(name = "tin_chi")
        private int tinChi;
        @ManyToMany(mappedBy = "courses")  // mappedBy trỏ tới trường "courses" trong Ctdt
        private Set<Ctdt> ctdts = new HashSet<>();  // Quan hệ Many-to-Many với Ctdt

        @OneToMany(mappedBy = "course")
        @JsonIgnore
        private Set<Dangkihocphan> dangkihocphans;

        @OneToMany(mappedBy = "course")
        @JsonIgnore
        private Set<Clazz> clazzes;

        public Course(Set<Clazz> clazzes, Set<Dangkihocphan> dangkihocphans, Integer id, String maHocPhan, String name, int tinChi) {
            this.clazzes = clazzes;
            this.dangkihocphans = dangkihocphans;
            this.id = id;
            this.maHocPhan = maHocPhan;
            this.name = name;
            this.tinChi = tinChi;
        }

        public Course() {
        }

        public Set<Clazz> getClazzes() {
            return clazzes;
        }

        public Set<Ctdt> getCtdts() {
            return ctdts;
        }

        public void setCtdts(Set<Ctdt> ctdts) {
            this.ctdts = ctdts;
        }

        public void setClazzes(Set<Clazz> clazzes) {
            this.clazzes = clazzes;
        }

        public Set<Dangkihocphan> getDangkihocphans() {
            return dangkihocphans;
        }

        public void setDangkihocphans(Set<Dangkihocphan> dangkihocphans) {
            this.dangkihocphans = dangkihocphans;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getMaHocPhan() {
            return maHocPhan;
        }

        public void setMaHocPhan(String maHocPhan) {
            this.maHocPhan = maHocPhan;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getTinChi() {
            return tinChi;
        }

        public void setTinChi(int tinChi) {
            this.tinChi = tinChi;
        }


    }
