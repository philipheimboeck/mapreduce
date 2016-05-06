package at.phe.def.mapreduce;

import at.enfilo.def.prototype1.commons.PersistenceHandler;
import at.enfilo.def.prototype1.commons.PersistenceHandlerFactory;
import at.enfilo.def.prototype1.commons.exceptions.ResourceAccessException;
import at.enfilo.def.prototype1.commons.structs.TaskResult;
import at.enfilo.def.prototype1.commons.structs.TaskState;
import at.enfilo.def.prototype1.workermodule.DispatcherClient;
import at.phe.def.mapreduce.partitioner.HashPartitioner;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;

/**
 * Author: Philip Heimböck
 * Date: 06.05.16.
 */
public class MapTaskCallable implements Callable<List<String[]>> {

    protected DispatcherClient dispatcherClient = DispatcherClient.getInstance();
    protected PersistenceHandler persistenceHandler = PersistenceHandlerFactory.getPersistenceHandler();
    protected MapTaskDTO task;


    public MapTaskCallable(MapTaskDTO task) {
        this.task = task;
    }

    @Override
    public List<String[]> call() throws Exception {
        // This is a capsule of a task and a mapper that would run on one node

        // First run the application task
        TaskResult taskResult = dispatcherClient.runTask(task.getAppTask());
        if (taskResult.getState().equals(TaskState.ERROR)) {
            return null;
        }

        // Todo: Get rid of this step to spare one copy
        // Move task result to global program parameters where the map task can read it
        String mapInputReference = task.getMapTask().getInParameters().get(0);
        String taskResultData = persistenceHandler.readResult(task.getAppTask().getProgramId(), task.getAppTask().getJobId(), task.getAppTask().getId());
        persistenceHandler.writeResource(task.getAppTask().getProgramId(), mapInputReference, taskResultData);

        // Then run the mapper task on the app task result
        taskResult = dispatcherClient.runTask(task.getMapTask());
        if (taskResult.getState().equals(TaskState.ERROR)) {
            return null;
        }

        // Todo Move partitioner into map task to spare one copy
        // Then partition the map output
        TuplePartitioner partitioner = new TuplePartitioner(new HashPartitioner(), task.getNumberPartitions());
        partitioner.partition(task.getMapTask());

        // Save all partitions
        List<String[]> partitions = new ArrayList<>();
        partitioner.getPartitions().forEach(partition -> {
            String partitionKey = UUID.randomUUID().toString();
            String[] keys = {task.getId(), partitionKey};
            partitions.add(keys);
            try {
                persistenceHandler.writeResource(task.getAppTask().getProgramId(), task.getMapTask().getJobId(), task.getId(), partitionKey, partition.toString());
            } catch (ResourceAccessException e) {
                e.printStackTrace();
            }
        });

        return partitions;
    }
}
