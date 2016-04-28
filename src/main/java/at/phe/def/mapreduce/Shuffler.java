package at.phe.def.mapreduce;

import at.enfilo.def.prototype1.commons.DEFTypeConverter;
import at.enfilo.def.prototype1.commons.PersistenceHandler;
import at.enfilo.def.prototype1.commons.PersistenceHandlerFactory;
import at.enfilo.def.prototype1.commons.exceptions.ResourceAccessException;
import at.enfilo.def.prototype1.commons.exceptions.ResourceNotExistsException;
import at.enfilo.def.prototype1.commons.remote.TaskDTO;
import com.google.gson.JsonArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Philip Heimböck
 * Date: 23.04.16.
 */
public class Shuffler {

    protected int numberReducers = 0;
    protected List<JsonArray> shuffled = new ArrayList<>();

    protected PersistenceHandler persistenceHandler = PersistenceHandlerFactory.getPersistenceHandler();

    public Shuffler(int numberReducers) {
        this.numberReducers = numberReducers;

        for (int i = 0; i < numberReducers; i++) {
            shuffled.add(new JsonArray());
        }
    }

    public void shuffle(TaskDTO taskDTO) throws ResourceNotExistsException, ResourceAccessException {

        String taskResult = persistenceHandler.readResult(taskDTO.getProgramId(), taskDTO.getJobId(), taskDTO.getId());

        // Convert the task result into the tuples
        JsonArray mapResult = DEFTypeConverter.convert(taskResult, JsonArray.class);

        // Sort the mapResult // TODO Put it to the mapper
        // mapResult.getTuples().sort((a, b) -> a.value1.compareTo(b.value1));

        // Shuffle the results
        mapResult.forEach(tuple -> {
            // The tuple is another array
            JsonArray data = tuple.getAsJsonArray();
            // Choose always the same reducer for the same key
            int hash = Math.abs(data.get(0).hashCode());
            int chosenReducer = hash % numberReducers;
            shuffled.get(chosenReducer).add(data);
        });
    }

    public List<JsonArray> getShuffled() {
        return shuffled;
    }
}
