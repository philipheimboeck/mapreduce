package at.phe.def.mapreduce;

import at.enfilo.def.prototype1.commons.DEFTypeConverter;
import at.enfilo.def.prototype1.commons.PersistenceHandler;
import at.enfilo.def.prototype1.commons.PersistenceHandlerFactory;
import at.enfilo.def.prototype1.commons.exceptions.ResourceAccessException;
import at.enfilo.def.prototype1.commons.exceptions.ResourceNotExistsException;
import at.enfilo.def.prototype1.commons.remote.TaskDTO;
import at.phe.def.mapreduce.partitioner.IPartitioner;
import com.google.gson.JsonArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Philip Heimböck
 * Date: 23.04.16.
 */
public class TuplePartitioner {

    protected int numberReducers = 0;
    protected List<JsonArray> partitions = new ArrayList<>();
    protected IPartitioner partitioner;

    protected PersistenceHandler persistenceHandler = PersistenceHandlerFactory.getPersistenceHandler();

    public TuplePartitioner(IPartitioner partitioner, int numberReducers) {
        this.partitioner = partitioner;
        this.numberReducers = numberReducers;

        for (int i = 0; i < numberReducers; i++) {
            partitions.add(new JsonArray());
        }
    }

    public void partition(TaskDTO taskDTO) throws ResourceNotExistsException, ResourceAccessException {

        String taskResult = persistenceHandler.readResult(taskDTO.getProgramId(), taskDTO.getJobId(), taskDTO.getId());

        // Convert the task result into the tuples
        JsonArray mapResult = DEFTypeConverter.convert(taskResult, JsonArray.class);

        // Shuffle the results
        mapResult.forEach(tuple -> {
            // The tuple is another array
            JsonArray data = tuple.getAsJsonArray();

            // Choose always the same reducer for the same key
            int chosenReducer = partitioner.partition(data.get(0), numberReducers);
            partitions.get(chosenReducer).add(data);
        });
    }

    public List<JsonArray> getPartitions() {
        return partitions;
    }
}
