package at.phe.def.mapreduce;

import at.enfilo.def.prototype1.commons.properties.DEFProperty;
import at.enfilo.def.prototype1.commons.properties.PropertyManager;
import at.enfilo.def.prototype1.commons.remote.TaskDTO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Author: Philip Heimböck
 * Date: 06.05.16.
 * <p>
 * Capsule object for MapReduce jobs
 */
public class MapTaskDTO implements Serializable {
    private static final String defaultPartitioner = PropertyManager.getInstance().getProperty(DEFProperty.MAP_REDUCE_PARTITIONER);
    private String id;
    private TaskDTO appTask;
    private TaskDTO mapTask;
    private TaskDTO partitionTask;
    private TaskDTO combinerTask;

    // Equal to the number of reducers
    private int numberPartitions;

    public MapTaskDTO(TaskDTO appTask, String mapLibrary, int numberPartitions) {
        this(UUID.randomUUID().toString(), appTask, mapLibrary, null, null, numberPartitions);
    }

    public MapTaskDTO(TaskDTO appTask, String mapLibrary, String partitionLibrary, int numberPartitions) {
        this(UUID.randomUUID().toString(), appTask, mapLibrary, partitionLibrary, null, numberPartitions);
    }

    public MapTaskDTO(TaskDTO appTask, String mapLibrary, String partitionLibrary, String combinerLibrary, int numberPartitions) {
        this(UUID.randomUUID().toString(), appTask, mapLibrary, partitionLibrary, combinerLibrary, numberPartitions);
    }

    public MapTaskDTO(String id, TaskDTO appTask, String mapLibrary, String partitionLibrary, String combinerLibrary, int numberPartitions) {
        this.id = id;
        this.appTask = appTask;
        this.numberPartitions = numberPartitions;

        // Create Map Task
        List<String> inParameters = new ArrayList<>();
        inParameters.add('?' + id); // First parameter is always the input reference
        this.mapTask = new TaskDTO(UUID.randomUUID().toString(), appTask.getProgramId(), appTask.getJobId(), mapLibrary, inParameters, "");

        // Create Partition Task
        if (partitionLibrary == null) {
            // Use the default partition library
            partitionLibrary = defaultPartitioner;
        }
        List<String> inParametersPartition = new ArrayList<>();
        inParametersPartition.add(String.valueOf(numberPartitions)); // First parameter is always the number of reducers (partitions)
        inParametersPartition.add('?' + id); // Second parameter is always the input reference
        this.partitionTask = new TaskDTO(UUID.randomUUID().toString(), appTask.getProgramId(), appTask.getJobId(), partitionLibrary, inParametersPartition, "");

        // Create Combiner Task if given
        if (combinerLibrary != null) {
            List<String> inParametersCombiner = new ArrayList<>();
            inParametersCombiner.add('?' + id); // First parameter is always the input reference
            this.combinerTask = new TaskDTO(UUID.randomUUID().toString(), appTask.getProgramId(), appTask.getJobId(), combinerLibrary, inParametersCombiner, "");
        }
    }

    public String getId() {
        return id;
    }

    public TaskDTO getAppTask() {
        return appTask;
    }

    public TaskDTO getMapTask() {
        return mapTask;
    }

    public int getNumberPartitions() {
        return numberPartitions;
    }

    public TaskDTO getPartitionTask() {
        return partitionTask;
    }

    public TaskDTO getCombinerTask() {
        return combinerTask;
    }
}
