package at.phe.def.mapreduce;

import at.enfilo.def.prototype1.workermodule.DispatcherClient;

import java.util.concurrent.Callable;

/**
 * Author: Philip Heimböck
 * Date: 06.05.16.
 */
public class ReduceTaskCallable implements Callable<ReduceTaskDTO> {

    protected DispatcherClient dispatcherClient = DispatcherClient.getInstance();
    protected ReduceTaskDTO task;

    public ReduceTaskCallable(ReduceTaskDTO task) {
        this.task = task;
    }

    @Override
    public ReduceTaskDTO call() throws Exception {

        // Run the task
        dispatcherClient.runTask(task.task);

        return task;
    }
}
