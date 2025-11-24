package com.github.suzu_devworks.examples.localization;

import java.time.OffsetDateTime;
import java.time.ZoneId;

public class DateTimeConvertor {

    public static OffsetDateTime now(ZoneId zone) {
        return atZoneSameInstant(OffsetDateTime.now(), zone);
    }

    public static OffsetDateTime atZoneSameInstant(OffsetDateTime o, ZoneId zone) {

        if (o == null) {
            return null;
        }

        if (zone == null) {
            zone = ZoneId.systemDefault();
        }

        return o.atZoneSameInstant(zone).toOffsetDateTime();
    }
}
