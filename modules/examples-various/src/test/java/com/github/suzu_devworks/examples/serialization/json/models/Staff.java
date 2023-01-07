package com.github.suzu_devworks.examples.serialization.json.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class Staff {
    private String name;
    private int numOfYears;
    private String[] position; // array
    private List<String> skills; // list
    private Map<String, BigDecimal> salary; // map
    private LocalDate birthday;
    private ZonedDateTime lastUpdateAt;

    @JsonIgnore
    private TimeZone localeTimeZone;
}