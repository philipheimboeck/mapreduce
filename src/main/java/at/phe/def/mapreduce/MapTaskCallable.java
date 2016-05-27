package at.phe.def.mapreduce;

import at.enfilo.def.prototype1.commons.PersistenceHandler;
import at.enfilo.def.prototype1.commons.PersistenceHandlerFactory;
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

        // First run the application task
        TaskResult taskResult = dispatcherClient.runTask(task.getAppTask());
        if (taskResult.getState().equals(TaskState.ERROR)) {
            return taskResult;
        }

        // Todo: Get rid of this step to spare one copy
        // Move task result to global program parameters where the map task can read it
        String mapInputReference = task.getId();
        String taskResultData = persistenceHandler.readResult(task.getAppTask().getProgramId(), task.getAppTask().getJobId(), task.getAppTask().getId());
        persistenceHandler.writeResource(task.getAppTask().getProgramId(), task.getAppTask().getJobId(), mapInputReference, taskResultData);

        // Then run the mapper task on the app task result
        return dispatcherClient.runTask(task.getMapTask());
    }
}
