package at.phe.def.mapreduce.base;

import at.enfilo.def.prototype1.commons.DEFTypeConverter;
import com.google.gson.JsonArray;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Author: Philip Heimböck
 * Date: 06.05.16.
 */
public abstract class ReduceJavaBaseLibraryFunction<Key, Value, KeyOut, ValueOut> extends JavaBaseLibraryFunction {

    protected HashMap<KeyOut, ValueOut> result = new HashMap<>();

    Class<Key> keyClass;
    Class<Value> valueClass;

    public ReduceJavaBaseLibraryFunction(Class<Key> keyClass, Class<Value> valueClass) {
        this.keyClass = keyClass;
        this.valueClass = valueClass;
    }

    @Override
    public void run(List<String> parameters) throws Exception {
        HashMap<Key, Collection<Value>> mergedPartitions = new HashMap<>();

        // Merge the partitions
        parameters.forEach(partition -> {
                    // Every partition is actually a JsonArray
                    JsonArray jsonArray = DEFTypeConverter.convert(partition, JsonArray.class);

                    // For every tuple add the key and value to the merged partitions
                    jsonArray.forEach(element -> {
                        // Every tuple is also a JsonArray
                        JsonArray tuple = element.getAsJsonArray();

                        // For every tuple add the key and value to the merged partitions
                        // Every partitions contains one key and one value
                        Key key = DEFTypeConverter.convert(tuple.get(0).getAsJsonPrimitive(), keyClass);
                        Value value = DEFTypeConverter.convert(tuple.get(1).getAsJsonPrimitive(), valueClass);

                        if(!mergedPartitions.containsKey(key)) {
                            mergedPartitions.put(key, new ArrayList<>());
                        }
                        mergedPartitions.get(key).add(value);
                    });
                }
        );

        // Run the reducer function
        for(Key key : mergedPartitions.keySet()) {
            runReduce(key, mergedPartitions.get(key));
        }

        setResult(result);
    }

    /**
     * Emit a new tuple
     *
     * @param key   The key of the tuple
     * @param value The value of the tuple
     */
    protected void emit(KeyOut key, ValueOut value) {
        result.put(key, value);
    }

    /**
     * Run the reduce function
     *
     * @param key
     * @param values
     */
    protected abstract void runReduce(Key key, Collection<Value> values);
}
