package at.phe.def.mapreduce;

import at.enfilo.def.prototype1.commons.remote.TaskDTO;

import java.io.Serializable;
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

    public MapTaskDTO(TaskDTO appTask, TaskDTO mapTask, int numberPartitions) {
        this(UUID.randomUUID().toString(), appTask, mapTask, numberPartitions);
    }

    public MapTaskDTO(String id, TaskDTO appTask, TaskDTO mapTask, int numberPartitions) {
        this.id = id;
        this.appTask = appTask;
        this.mapTask = mapTask;
        this.numberPartitions = numberPartitions;
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
