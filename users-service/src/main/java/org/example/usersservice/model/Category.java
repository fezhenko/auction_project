package org.example.usersservice.model;

import javax.validation.constraints.NotNull;

import lombok.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

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
