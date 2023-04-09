package org.example.usersservice.model;

import lombok.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Value
@Table("currencies")
public class Currency {
    @Id
    @Column("id")
    Long id;

    @Column("currency")
    Object currency;
}
