package at.phe.def.mapreduce;

import at.enfilo.def.prototype1.commons.remote.TaskDTO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Author: Philip Heimböck
 * Date: 06.05.16.
 */
public class ReduceTaskDTO implements Serializable {
    protected TaskDTO task;

    public ReduceTaskDTO(String programId, String jobId, String reduceFunction, List<String> partitionsResources) {
        List<String> inParameters = partitionsResources.stream()
                .map(partition -> '?' + partition)
                .collect(Collectors.toList());

        this.task = new TaskDTO(UUID.randomUUID().toString(), programId, jobId, reduceFunction, inParameters, "");
    }
}
