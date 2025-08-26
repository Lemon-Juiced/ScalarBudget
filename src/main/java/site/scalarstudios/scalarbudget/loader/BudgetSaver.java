package site.scalarstudios.scalarbudget.loader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import site.scalarstudios.scalarbudget.model.BudgetItem;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * The BudgetSaver class provides functionality to save budget items to a JSON file.
 * It handles serialization and error management, returning a boolean status of the operation.
 *
 * @author Lemon_Juiced
 */
public class BudgetSaver {
    public static boolean saveBudgetItems(File file, List<BudgetItem> items) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            mapper.writeValue(file, items);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}

