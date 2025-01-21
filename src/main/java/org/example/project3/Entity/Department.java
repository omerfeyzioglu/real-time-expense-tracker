package org.example.project3.Entity;



import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name="dept")
public class Department {

    @Id
    @Column(name = "deptno")
    private int deptno;

    @Column(name = "dname")
    private String dname;

    @Column(name="loc")
    private String loc;

    @OneToMany(mappedBy = "dept", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Employee> employees;



}

