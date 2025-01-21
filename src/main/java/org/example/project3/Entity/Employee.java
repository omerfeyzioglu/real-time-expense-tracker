package org.example.project3.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "emp")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "emp_seq")
    @SequenceGenerator(name = "emp_seq", sequenceName = "emp_seq", allocationSize = 1)
    @Column(name="empno")
    private Integer empno;

    @Column(name="ename")
    private String ename;

    @Column(name="job")
    private String job;

    @Column(name="mgr")
    private Integer mgr;

    @Column(name="hiredate")
    private String hiredate;

    @Column(name="sal")
    private Double sal;

    @Column(name="comm")
    private Double comm;

    @Column(name="deptno")
    private Integer deptno;

    @Column(name="img")
    private String img;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deptno", referencedColumnName = "deptno", insertable = false, updatable = false)
    @JsonBackReference
    private Department dept;


    @Override
    public String toString() {
        return String.format("Employee(empno=%d, ename=%s, job=%s, sal=%.2f)",
                empno, ename, job, sal);
    }

}

