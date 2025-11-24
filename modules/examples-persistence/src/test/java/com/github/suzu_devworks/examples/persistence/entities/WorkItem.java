package com.github.suzu_devworks.examples.persistence.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@ToString
@Builder
@Data
public class WorkItem {

    private Integer id;

    private String code;

    private String name;

    private BigDecimal price;

    private Integer quantity;

    private LocalDate purchaseDate;

    private OffsetDateTime lastUpdatedAt;

}
