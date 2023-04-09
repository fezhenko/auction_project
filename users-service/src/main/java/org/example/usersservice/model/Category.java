package org.example.usersservice.model;

import lombok.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.NotNull;

@Value
@Table("categories")
public class Category {
    @Id
    @Column("id")
    @NotNull
    Long id;
    @Column("name")
    String categoryName;
}
