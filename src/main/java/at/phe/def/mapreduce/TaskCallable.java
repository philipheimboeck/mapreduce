package at.phe.def.mapreduce;

import at.enfilo.def.prototype1.commons.structs.TaskResult;
import at.enfilo.def.prototype1.domain.Task;
import at.enfilo.def.prototype1.workermodule.DispatcherClient;

import java.util.concurrent.Callable;

/**
 * Author: Philip Heimböck
 * Date: 06.05.16.
 */
public class TaskCallable implements Callable<TaskResult> {

    protected DispatcherClient dispatcherClient = DispatcherClient.getInstance();
    protected Task task;

    public TaskCallable(Task task) {
        this.task = task;
    }

    @Override
    public TaskResult call() throws Exception {
        // Run the task
        return dispatcherClient.runTask(task.getDTO());
    }
}
