package com.github.suzu_devworks.examples.serialization.json.models;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
@JsonDeserialize(builder = Team.TeamBuilder.class)
public class Team {
    @JsonProperty
    private final String name;
    @JsonProperty
    private final LocalDate startAt;
    @JsonProperty
    private final List<Staff> members;
}
