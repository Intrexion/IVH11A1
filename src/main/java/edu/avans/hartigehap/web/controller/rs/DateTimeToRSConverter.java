package edu.avans.hartigehap.web.controller.rs;

import java.io.IOException;
import java.lang.reflect.Type;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class DateTimeToRSConverter extends JsonSerializer<DateTime> {
    private static DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");

        @Override
        public void serialize(DateTime arg0, JsonGenerator gen, 
                              SerializerProvider arg2)
            throws IOException, JsonProcessingException {

            gen.writeString(formatter.print(arg0));
        }
}
