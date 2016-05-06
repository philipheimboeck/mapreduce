package at.phe.def.mapreduce;

import at.enfilo.def.prototype1.commons.DEFTypeConverter;
import at.enfilo.def.prototype1.commons.PersistenceHandler;
import at.enfilo.def.prototype1.commons.PersistenceHandlerFactory;
import at.enfilo.def.prototype1.commons.exceptions.ResourceAccessException;
import at.enfilo.def.prototype1.commons.exceptions.ResourceNotExistsException;
import at.enfilo.def.prototype1.workermodule.DispatcherClient;
import com.google.gson.JsonArray;

import java.util.concurrent.Callable;

/**
 * Author: Philip Heimböck
 * Date: 06.05.16.
 */
public class ReduceTaskCallable implements Callable<ReduceTaskDTO> {

    protected DispatcherClient dispatcherClient = DispatcherClient.getInstance();
    protected PersistenceHandler persistenceHandler = PersistenceHandlerFactory.getPersistenceHandler();
    protected ReduceTaskDTO task;

    public ReduceTaskCallable(ReduceTaskDTO task) {
        this.task = task;
    }

    @Override
    public ReduceTaskDTO call() throws Exception {
        // Todo: Move that to the reducers
        // Merge all partitions that belong together
        JsonArray resources = new JsonArray();
        for (String[] keys : task.partitionsResources) {
            try {
                String partition = persistenceHandler.readResource(task.task.getProgramId(), task.task.getJobId(), keys[0], keys[1]);
                JsonArray tuples = DEFTypeConverter.convert(partition, JsonArray.class);

                tuples.forEach(resources::add);

            } catch (ResourceNotExistsException | ResourceAccessException e) {
                e.printStackTrace();
            }
        }

        // Todo Move the merge to the reducers
        // Save the merged partitions so that we submit only the reference to the reduce library
        String dataReference = task.task.getInParameters().get(0);
        persistenceHandler.writeResource(task.task.getProgramId(), dataReference, resources.toString());

        // Run the task
        dispatcherClient.runTask(task.task);

        return task;
    }
}
