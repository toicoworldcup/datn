    package org.example.doantn.Entity;

    import jakarta.persistence.*;
    import lombok.Getter;
    import lombok.Setter;

    import java.util.HashSet;
    import java.util.List;
    import java.util.Set;

    @Setter
    @Getter
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
        @ManyToOne
        @JoinColumn(name = "batch_id") // hoặc tên cột phù hợp với cơ sở dữ liệu
        private Batch batch;

        @ManyToMany
        @JoinTable(
                name = "course_ctdt",
                joinColumns = @JoinColumn(name = "ctdt_id"),
                inverseJoinColumns = @JoinColumn(name = "course_id")
        )
        private Set<Course> courses = new HashSet<>();

        public Ctdt() {
        }

    }
