package com.github.suzu_devworks.examples.serialization.json.models;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public final class TeamFactory {

    public static String getJson() {
        var classLoader = TeamFactory.class.getClassLoader();
        var path = Paths.get(classLoader.getResource("serialization/json/team-data.json").getFile());
        try {
            return Files.readString(path, Charset.forName("UTF-8"));
        } catch (IOException e) {
            System.err.println(e.toString());
            return null;
        }
    }

    public static Team getTeam() {
        var staff = new Staff();
        staff.setName("Bob");
        staff.setNumOfYears(35);
        staff.setPosition(new String[] { "Founder", "CTO", "Writer" });

        Map<String, BigDecimal> salary = new HashMap<>() {
            {
                put("2010", new BigDecimal(10000));
                put("2012", new BigDecimal(12000));
                put("2018", new BigDecimal(14000));
            }
        };
        staff.setSalary(salary);
        staff.setSkills(Arrays.asList("java", "python", "node", "kotlin"));
        staff.setBirthday(LocalDate.of(1970, 4, 1));
        staff.setLastUpdateAt(ZonedDateTime.of(2018, 1, 12, 13, 14, 15,
                167000000, ZoneId.of("Asia/Tokyo")));

        staff.setLocaleTimeZone(TimeZone.getDefault());

        var team = Team.builder()
                .name("Avoid project.")
                .startAt(LocalDate.of(2020, 9, 1))
                .members(Arrays.asList(staff))
                .build();

        return team;
    }
}
