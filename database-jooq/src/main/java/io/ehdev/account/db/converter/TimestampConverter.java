package io.ehdev.account.db.converter;

import org.jooq.Converter;

import java.sql.Timestamp;
import java.time.Instant;

public class TimestampConverter implements Converter<Timestamp, Instant> {
    @Override public Instant from(Timestamp ts) {
        return ts == null ? null : ts.toInstant();
    }
    @Override public Timestamp to(Instant instant) {
        return instant == null ? null : Timestamp.from(instant);
    }
    @Override public Class<Timestamp> fromType() { return Timestamp.class; }
    @Override public Class<Instant> toType() { return Instant.class; }
}
