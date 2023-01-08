package com.github.suzu_devworks.examples.localization;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class DateTimeConvertor {

    public static ZonedDateTime toZonedDateTime(OffsetDateTime o, ZoneId zoneId) {
        return (o != null) ? o.atZoneSameInstant(zoneId) : null;
    }
}
