package at.phe.def.mapreduce.base;

import at.enfilo.def.prototype1.commons.DEFTypeConverter;
import at.enfilo.def.prototype1.commons.exceptions.ResourceAccessException;
import at.phe.def.mapreduce.TuplePartitioner;
import at.phe.def.mapreduce.demo.JavaBaseLibraryFunction;
import at.phe.def.mapreduce.partitioner.HashPartitioner;
import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Author: Philip Heimböck
 * Date: 06.05.16.
 */
public abstract class MapJavaBaseLibraryFunction<Key extends JsonPrimitive, Value extends JsonPrimitive> extends JavaBaseLibraryFunction {

    protected JsonArray result = new JsonArray();
    protected Integer numberPartitions;

    @Override
    public void run(List<String> parameters) throws Exception {

        // First parameter is the number of partitions to be created
        numberPartitions = DEFTypeConverter.convert(parameters.get(0), Integer.class);

        List<String> input = parameters.subList(1, parameters.size());

        // Run the map function
        runMap(input);

        // Partition the results
        List<JsonArray> partitions = partition(result);

        // Save all partitions
        List<String> references = new ArrayList<>();
        for (JsonArray partition : partitions) {
            references.add(writePartition(partition));
        }

        // Save the references as result
        setResult(references);
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
     * Partition the result
     * @param value The JsonArray that will be partitioned
     * @return all partitions
     */
    protected List<JsonArray> partition(JsonArray value) {
        // Partition the result
        TuplePartitioner partitioner = new TuplePartitioner(new HashPartitioner(), numberPartitions);
        partitioner.partition(value);

        return partitioner.getPartitions();
    }

    /**
     * Persist the partition
     * @param partition The partition to save
     * @return The key (reference) for the partition
     */
    protected String writePartition(JsonArray partition) throws ResourceAccessException {

        String partitionKey = UUID.randomUUID().toString();
        writeJobResource(partition, partitionKey);

        return partitionKey;
    }

    /**
     * Run the map function
     *
     * @param parameters The input parameters
     */
    protected abstract void runMap(List<String> parameters);
}
