package com.github.suzu_devworks.examples.serialization.json;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import com.github.suzu_devworks.examples.serialization.gson.AnnotationExclusionStrategy;
import com.github.suzu_devworks.examples.serialization.gson.LocalDateTimeTypeAdapter;
import com.github.suzu_devworks.examples.serialization.gson.LocalDateTypeAdapter;
import com.github.suzu_devworks.examples.serialization.gson.ZonedDateTimeTypeAdapter;
import com.github.suzu_devworks.examples.serialization.json.models.Team;
import com.github.suzu_devworks.examples.serialization.json.models.TeamFactory;

public class GsonTests {

    private Gson gson;

    @BeforeEach
    void setUp() {
        // Gson gson = new Gson();
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class,
                        new LocalDateTimeTypeAdapter().nullSafe())
                .registerTypeAdapter(LocalDate.class,
                        new LocalDateTypeAdapter().nullSafe())
                .registerTypeAdapter(ZonedDateTime.class,
                        new ZonedDateTimeTypeAdapter().nullSafe())
                // .excludeFieldsWithoutExposeAnnotation() // enable @Expose()
                .setExclusionStrategies(new AnnotationExclusionStrategy())
                // .enableComplexMapKeySerialization()
                // .serializeNulls()
                // .setDateFormat(DateFormat.LONG) // do not work.
                .setFieldNamingPolicy(
                        FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                // .setPrettyPrinting()
                .setVersion(1.0) // enable @Since()
                .create();
    }

    @Test
    void whenSerializing() throws IOException, JSONException {
        final var team = TeamFactory.getTeam();
        final var expected = TeamFactory.getJson();

        var actual = gson.toJson(team);

        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @Test
    void whenDeserializing() {
        final var expected = TeamFactory.getTeam();
        final var data = TeamFactory.getJson();

        var actual = gson.fromJson(data, Team.class);

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

    @Test
    void whenSerializing_withDoubleBrace() throws IOException {
        /*
         * final Team team = new Team();
         * team.setName("normal init.");
         *
         * final Team doubleBrace = new Team() {{
         * setName("double");
         * }};
         *
         * final String actual = gson.toJson(team);
         * final String actual2 = gson.toJson(doubleBrace);
         *
         * JSONAssert.assertEquals("{\"name\":\"normal init.\"}", actual,
         * JSONCompareMode.STRICT);
         * assertEquals("null", actual2);
         */
        return;
    }
}
