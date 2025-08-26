package site.scalarstudios.scalarbudget.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;

/**
 * Custom deserializer for the Period enum.
 * Converts a JSON string representation of a period into the corresponding Period enum value.
 * Defaults to Period.MONTH if the string does not match any known values.
 *
 * @author Lemon_Juiced
 */
public class PeriodDeserializer extends JsonDeserializer<Period> {
    @Override
    public Period deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getText();
        return Period.fromString(value).orElse(Period.MONTH);
    }
}

