package at.phe.def.mapreduce;

import at.enfilo.def.prototype1.commons.remote.TaskDTO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Author: Philip Heimböck
 * Date: 06.05.16.
 *
 * Capsule object for MapReduce jobs
 */
public class MapTaskDTO implements Serializable {
    private String id;
    private TaskDTO appTask;
    private TaskDTO mapTask;

    // Equal to the number of reducers
    private int numberPartitions;

    public MapTaskDTO(TaskDTO appTask, String mapLibrary, int numberPartitions) {
        this(UUID.randomUUID().toString(), appTask, mapLibrary, numberPartitions);
    }

    public MapTaskDTO(String id, TaskDTO appTask, String mapLibrary, int numberPartitions) {
        this.id = id;
        this.appTask = appTask;
        this.numberPartitions = numberPartitions;

        // Create Map Task
        List<String> inParameters = new ArrayList<>();
        inParameters.add(String.valueOf(numberPartitions)); // First parameter is always the number of reducers
        inParameters.add('?' + id); // Second parameter is always the input reference

        this.mapTask = new TaskDTO(UUID.randomUUID().toString(), appTask.getProgramId(), appTask.getJobId(), mapLibrary, inParameters, "");
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TaskDTO getAppTask() {
        return appTask;
    }

    public void setAppTask(TaskDTO appTask) {
        this.appTask = appTask;
    }

    public TaskDTO getMapTask() {
        return mapTask;
    }

    public void setMapTask(TaskDTO mapTask) {
        this.mapTask = mapTask;
    }

    public int getNumberPartitions() {
        return numberPartitions;
    }

    public void setNumberPartitions(int numberPartitions) {
        this.numberPartitions = numberPartitions;
    }
}
