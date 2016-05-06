package at.phe.def.mapreduce;

import at.enfilo.def.prototype1.commons.remote.TaskDTO;

import java.util.List;

/**
 * Author: Philip Heimböck
 * Date: 06.05.16.
 */
public class ReduceTaskDTO {
    protected TaskDTO task;
    protected List<String[]> partitionsResources;

    public ReduceTaskDTO(TaskDTO task, List<String[]> partitionsResources) {
        this.task = task;
        this.partitionsResources = partitionsResources;
    }
}
