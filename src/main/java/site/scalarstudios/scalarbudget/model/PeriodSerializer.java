package site.scalarstudios.scalarbudget.model;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;

/**
 * Custom serializer for the Period enum.
 * Converts a Period enum value into its string representation for JSON output.
 *
 * @author Lemon_Juiced
 */
public class PeriodSerializer extends JsonSerializer<Period> {
    @Override
    public void serialize(Period value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(value.getDisplayName());
    }
}

