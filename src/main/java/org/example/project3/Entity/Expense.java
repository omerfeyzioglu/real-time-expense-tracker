package org.example.project3.Entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.cassandra.core.mapping.Table;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Column;

@Getter
@Setter
@Table("expenses")
public class Expense {
    @PrimaryKey
    private String empno;

    @Column("type")
    private String type;

    @Column("payment")
    private Double payment;


    private String description;

    @Column("date_time")
    private String dateTime;

    @Column("count")
    private Integer count;
    // Getters and setters

}
