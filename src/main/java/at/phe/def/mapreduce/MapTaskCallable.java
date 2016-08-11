package at.phe.def.mapreduce;

import at.enfilo.def.prototype1.commons.PersistenceHandler;
import at.enfilo.def.prototype1.commons.PersistenceHandlerFactory;
import at.enfilo.def.prototype1.commons.exceptions.ResourceAccessException;
import at.enfilo.def.prototype1.commons.exceptions.ResourceNotExistsException;
import at.enfilo.def.prototype1.commons.remote.TaskDTO;
import at.enfilo.def.prototype1.commons.structs.TaskResult;
import at.enfilo.def.prototype1.commons.structs.TaskState;
import at.enfilo.def.prototype1.workermodule.DispatcherClient;

import java.util.concurrent.Callable;

/**
 * Author: Philip Heimböck
 * Date: 06.05.16.
 */
public class MapTaskCallable implements Callable<TaskResult> {

    protected DispatcherClient dispatcherClient = DispatcherClient.getInstance();
    protected PersistenceHandler persistenceHandler = PersistenceHandlerFactory.getPersistenceHandler();
    protected MapTaskDTO task;


    public MapTaskCallable(MapTaskDTO task) {
        this.task = task;
    }

    @Override
    public TaskResult call() throws Exception {
        // This is a capsule of a task and a mapper that would run on one node

        String mapInputReference = task.getId();

        // First run the application task
        TaskResult taskResult = dispatcherClient.runTask(task.getAppTask());
        if (taskResult.getState().equals(TaskState.ERROR)) {
            return taskResult;
        }

        // Move task result to global program parameters where the map task can read it
        moveTaskResult(taskResult.getTaskId(), mapInputReference);

        // Then run the mapper task on the app task result
        taskResult = dispatcherClient.runTask(task.getMapTask());
        if (taskResult.getState().equals(TaskState.ERROR)) {
            return taskResult;
        }

        // Run the combiner
        if(task.getCombinerTask() != null) {
            // Move task result to global program parameters where the combiner task can read it
            moveTaskResult(taskResult.getTaskId(), mapInputReference);

            taskResult = dispatcherClient.runTask(task.getCombinerTask());
            if (taskResult.getState().equals(TaskState.ERROR)) {
                return taskResult;
            }
        }

        // Move task result to global program parameters where the partition task can read it
        moveTaskResult(taskResult.getTaskId(), mapInputReference);

        // Run the partitioner
        taskResult = dispatcherClient.runTask(task.getPartitionTask());

        return taskResult;
    }

    /**
     * Moves the result of one task to the input of another
     * Todo: Get rid of this function to spare the copies. Instead let the tasks access the results directly!
     *
     * @param sourceTaskId
     * @param destination
     * @throws ResourceNotExistsException
     * @throws ResourceAccessException
     */
    private void moveTaskResult(String sourceTaskId, String destination) throws ResourceNotExistsException, ResourceAccessException {
        String taskResultData = persistenceHandler.readResult(task.getAppTask().getProgramId(), task.getAppTask().getJobId(), sourceTaskId);
        persistenceHandler.writeResource(task.getAppTask().getProgramId(), task.getAppTask().getJobId(), destination, taskResultData);
    }
}
