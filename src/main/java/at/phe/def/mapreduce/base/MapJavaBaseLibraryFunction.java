package at.phe.def.mapreduce.base;

import at.enfilo.def.prototype1.commons.exceptions.ResourceAccessException;
import at.phe.def.mapreduce.demo.JavaBaseLibraryFunction;
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
        runMap(parameters);
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

    @Override
    protected <T> void setResult(T value) throws ResourceAccessException {
        // Todo Partition the result

        super.setResult(value);
    }

    /**
     * Run the map function
     *
     * @param parameters
     */
    protected abstract void runMap(List<String> parameters);
}
