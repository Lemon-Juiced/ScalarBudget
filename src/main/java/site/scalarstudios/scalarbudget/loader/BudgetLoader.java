package site.scalarstudios.scalarbudget.loader;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import site.scalarstudios.scalarbudget.model.BudgetItem;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * The BudgetLoader class provides functionality to load budget items from a JSON file.
 * It handles file validation and error management, returning an empty list in case of issues.
 *
 * @author Lemon_Juiced
 */
public class BudgetLoader {
    public static List<BudgetItem> loadBudgetFile(File file) {
        if (file == null || !file.exists() || !file.canRead()) {
            return Collections.emptyList();
        }
        try {
            return parseBudgetItems(file);
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    private static List<BudgetItem> parseBudgetItems(File file) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(file, new TypeReference<List<BudgetItem>>() {});
    }
}
