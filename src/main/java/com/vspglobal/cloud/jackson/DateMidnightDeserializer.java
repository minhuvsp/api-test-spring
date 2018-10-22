package com.vspglobal.cloud.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public class DateMidnightDeserializer extends JsonDeserializer {
    private static final DateTimeFormatter FMT = ISODateTimeFormat.date();

    public DateMidnightDeserializer() {
    }

    public Object deserialize(JsonParser arg0, DeserializationContext arg1) throws IOException, JsonProcessingException {
        return arg0.getText().trim().isEmpty() ? null : FMT.parseDateTime(arg0.getText()).toDateMidnight();
    }
}
