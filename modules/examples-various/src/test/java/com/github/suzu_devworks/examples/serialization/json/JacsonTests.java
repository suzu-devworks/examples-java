package com.github.suzu_devworks.examples.serialization.json;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.IOException;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.suzu_devworks.examples.serialization.jackson.UTCZonedDateTimeSerializer;
import com.github.suzu_devworks.examples.serialization.json.models.Team;
import com.github.suzu_devworks.examples.serialization.json.models.TeamFactory;

import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

public class JacsonTests {

    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper()
                // .enable(SerializationFeature.INDENT_OUTPUT)
                // use java 8 Date and Time APIs.
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

        var module = new SimpleModule();
        module.addSerializer(ZonedDateTime.class,
                new UTCZonedDateTimeSerializer());
        mapper.registerModule(module);

    }

    @Test
    void whenSerializing() throws IOException, JSONException {
        final var team = TeamFactory.getTeam();
        final var expected = TeamFactory.getJson();

        var actual = mapper.writeValueAsString(team);

        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);

    }

    @Test
    void whenDeserializing() throws JsonMappingException, JsonProcessingException {
        final var expected = TeamFactory.getTeam();
        final var data = TeamFactory.getJson();

        var actual = mapper.readValue(data, Team.class);

        assertNotNull(actual);
        assertNotEquals(expected, actual);

        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getStartAt(), actual.getStartAt());
        assertEquals(expected.getMembers().size(), actual.getMembers().size());

        final var expectedStaff = expected.getMembers().get(0);
        var actualStaff = actual.getMembers().get(0);

        assertEquals(expectedStaff.getName(), actualStaff.getName());
        assertEquals(expectedStaff.getNumOfYears(),
                actualStaff.getNumOfYears());

        assertArrayEquals(expectedStaff.getPosition(),
                actualStaff.getPosition());
        // List<>, Map<> is work probably thanks to toString() method override.
        assertEquals(expectedStaff.getSalary(), actualStaff.getSalary());
        assertEquals(expectedStaff.getSkills(), actualStaff.getSkills());

        assertEquals(expectedStaff.getBirthday(), actualStaff.getBirthday());
        assertEquals(expectedStaff.getLastUpdateAt(),
                actualStaff.getLastUpdateAt()
                        .withZoneSameInstant(ZoneId.of("Asia/Tokyo")));

        assertNull(actualStaff.getLocaleTimeZone());
    }
}
