package com.clipboard.clipboard_store.utils;

import org.springframework.core.convert.converter.Converter;

import javax.validation.constraints.NotNull;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;

public class ZonedDateTimeReadConverter implements Converter<Date, ZonedDateTime> {

    @Override
    public ZonedDateTime convert(@NotNull Date date) {
        return date.toInstant().atZone(ZoneOffset.UTC);
    }
}