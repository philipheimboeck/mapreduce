package at.phe.def.mapreduce.base;

import at.enfilo.def.prototype1.commons.DEFTypeConverter;
import at.enfilo.def.prototype1.commons.exceptions.ResourceAccessException;
import at.phe.def.mapreduce.TuplePartitioner;
import at.phe.def.mapreduce.partitioner.IPartitioner;
import com.google.gson.JsonArray;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Author: Philip Heimböck
 * Date: 11.08.16.
 */
public abstract class PartitionerBaseLibraryFunction extends JavaBaseLibraryFunction {

    protected Integer numberPartitions;

    protected IPartitioner partitioner;

    public PartitionerBaseLibraryFunction(IPartitioner partitioner) {
        this.partitioner = partitioner;
    }

    @Override
    public void run(List<String> parameters) throws Exception {
        // First parameter is the number of partitions to be created
        numberPartitions = DEFTypeConverter.convert(parameters.get(0), Integer.class);

        // Second parameter is the result of the map/combiner task
        JsonArray input = DEFTypeConverter.convert(parameters.get(1), JsonArray.class);

        // Partition the input
        List<JsonArray> partitions = partition(input);

        // Save all partitions
        List<String> references = new ArrayList<>();
        for (JsonArray partition : partitions) {
            references.add(writePartition(partition));
        }

        // Save the references as result
        setResult(references);
    }

    /**
     * Partition the result
     *
     * @param value The JsonArray that will be partitioned
     * @return all partitions
     */
    protected List<JsonArray> partition(JsonArray value) {
        // Partition the result
        TuplePartitioner partitioner = new TuplePartitioner(this.partitioner, numberPartitions);
        partitioner.partition(value);

        return partitioner.getPartitions();
    }

    /**
     * Persist the partition
     *
     * @param partition The partition to save
     * @return The key (reference) for the partition
     */
    protected String writePartition(JsonArray partition) throws ResourceAccessException {

        String partitionKey = UUID.randomUUID().toString();
        writeJobResource(partition, partitionKey);

        return partitionKey;
    }
}
