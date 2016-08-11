package at.phe.def.mapreduce.base;

import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;

import java.util.List;

/**
 * Author: Philip Heimböck
 * Date: 06.05.16.
 */
public abstract class MapJavaBaseLibraryFunction<Key extends JsonPrimitive, Value extends JsonPrimitive> extends JavaBaseLibraryFunction {

    protected JsonArray result = new JsonArray();

    @Override
    public void run(List<String> parameters) throws Exception {
        // Run the map function
        runMap(parameters);

        // Save the result
        setResult(result);
    }

    /**
     * Emit a new tuple
     *
     * @param key   The key of the tuple
     * @param value The value of the tuple
     */
    protected void emit(Key key, Value value) {
        JsonArray tuple = new JsonArray();
        tuple.add(key);
        tuple.add(value);

        result.add(tuple);
    }

    /**
     * Run the map function
     *
     * @param parameters The input parameters
     */
    protected abstract void runMap(List<String> parameters);
}
