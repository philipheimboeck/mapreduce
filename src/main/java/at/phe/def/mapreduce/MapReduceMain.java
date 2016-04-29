package at.phe.def.mapreduce;

import at.enfilo.def.prototype1.commons.DEFTypeConverter;
import at.enfilo.def.prototype1.commons.PersistenceHandler;
import at.enfilo.def.prototype1.commons.PersistenceHandlerFactory;
import at.enfilo.def.prototype1.commons.exceptions.ResourceAccessException;
import at.enfilo.def.prototype1.commons.exceptions.ResourceNotExistsException;
import at.enfilo.def.prototype1.commons.remote.TaskDTO;
import at.enfilo.def.prototype1.commons.structs.TaskResult;
import at.enfilo.def.prototype1.commons.structs.TaskState;
import at.enfilo.def.prototype1.workermodule.DispatcherClient;
import at.phe.def.mapreduce.partitioner.HashPartitioner;
import com.google.gson.JsonArray;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * Author: Philip Heimböck
 * Date: 21.04.16.
 * <p>
 * Main Class for firing up a mapreduce job
 */
public class MapReduceMain {

    private static final String STORYTELLER_FUNCTION_ID = "6a2d8863-e96e-4d00-82b7-cc35ae390044"; // Java
    private static final String MAP_FUNCTION_ID = "b3499b28-cffa-4668-b974-311e8e767ecc"; // Java
//    private static final String MAP_FUNCTION_ID = "34cd23c4-f473-4010-a26e-69f56e6f3f83"; // Python
//        private static final String REDUCE_FUNCTION_ID = "e2497481-a342-40a3-9ece-24e88f5888f1"; // Java
    private static final String REDUCE_FUNCTION_ID = "e71fd934-fac4-4785-980c-6f0c0b79ec9a"; // Mono

    private ExecutorService executorService;
    private DispatcherClient dispatcherClient = DispatcherClient.getInstance();
    private PersistenceHandler persistenceHandler = PersistenceHandlerFactory.getPersistenceHandler();
    private String programId;
    private String jobId;

    public MapReduceMain(String programId, String jobId, int poolSize) {
        this.programId = programId;
        this.jobId = jobId;
        this.executorService = Executors.newFixedThreadPool(poolSize);
    }

    /**
     * Add all tasks to the threadpool and start them
     *
     * @param tasks
     */
    private void run(List<TaskDTO> tasks) {
        int numberReducers = 3;

        Collection<Callable<List<String[]>>> appTasks = new ArrayList<>();
        Collection<Callable<TaskResult>> reducerRunnables = new ArrayList<>();


        // Provide a list with runnable StoryTellers
        tasks.forEach(appTask -> appTasks.add(() -> {
                    // This is a capsule of a task and a mapper that would run on one node

                    // First run the application task
                    TaskResult taskResult = dispatcherClient.runTask(appTask);
                    if (taskResult.getState().equals(TaskState.ERROR)) {
                        return null;
                    }

                    // Todo: Get rid of this step
                    // Move task result to global program parameters
                    String taskResultData = persistenceHandler.readResult(programId, jobId, appTask.getId());
                    String taskResultResource = UUID.randomUUID().toString();
                    persistenceHandler.writeResource(programId, taskResultResource, taskResultData);

                    // Then run the mapper task on the app task result
                    TaskDTO mapperTask = createMapTask(taskResultResource);
                    taskResult = dispatcherClient.runTask(mapperTask);
                    if (taskResult.getState().equals(TaskState.ERROR)) {
                        return null;
                    }

                    // Then partition the map output
                    TuplePartitioner partitioner = new TuplePartitioner(new HashPartitioner(), numberReducers);
                    partitioner.partition(mapperTask);

                    // Save all partitions
                    List<String[]> partitions = new ArrayList<>();
                    partitioner.getPartitions().forEach(partition -> {
                        String partitionKey = UUID.randomUUID().toString();
                        String[] keys = {appTask.getId(), partitionKey};
                        partitions.add(keys);
                        try {
                            persistenceHandler.writeResource(programId, jobId, appTask.getId(), partitionKey, partition.toString());
                        } catch (ResourceAccessException e) {
                            e.printStackTrace();
                        }
                    });

                    return partitions;
                }
        ));

        try {
            // Run all app tasks and mappers
            final Collection<Future<List<String[]>>> reduceResources = executorService.invokeAll(appTasks);

            // Create reducers
            for (int i = 0; i < numberReducers; i++) {
                final int finalI = i;

                // This block is run in the reducer thread
                reducerRunnables.add(() -> {

                    // Merge all partitions that belong together
                    JsonArray resources = new JsonArray();
                    for(Future<List<String[]>> partitions : reduceResources) {
                        try {
                            String[] keys = partitions.get().get(finalI);
                            String partition = persistenceHandler.readResource(programId, jobId, keys[0], keys[1]);
                            JsonArray tuples = DEFTypeConverter.convert(partition, JsonArray.class);

                            tuples.forEach(resources::add);

                        } catch (InterruptedException | ExecutionException | ResourceNotExistsException | ResourceAccessException e) {
                            e.printStackTrace();
                        }
                    }

                    // Create the task
                    String dataReference = UUID.randomUUID().toString();
                    TaskDTO task = createReduceTask(dataReference);

                    // Save the merged partitions so that we submit only the reference to the reduce library
                    persistenceHandler.writeResource(programId, dataReference, resources.toString());

                    // RUn the task
                    TaskResult result = dispatcherClient.runTask(task);

                    // Print the output
                    printResult(task);

                    return result;
                });
            }

            // Start the reducers
            executorService.invokeAll(reducerRunnables);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        executorService.shutdown();
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void printResult(TaskDTO taskDTO) {
        try {
            System.out.println(persistenceHandler.readResult(programId, jobId, taskDTO.getId()));
        } catch (ResourceAccessException | ResourceNotExistsException e) {
            e.printStackTrace();
        }
    }

    private List<TaskDTO> createTasks(int numberTasks) {

        List<TaskDTO> tasks = new ArrayList<>();
        List<String> inParameters = new ArrayList<>();
        inParameters.add("1000"); // Number of sentences

        // Create tasks
        for (int i = 0; i < numberTasks; i++) {
            tasks.add(new TaskDTO(UUID.randomUUID().toString(), programId, jobId, STORYTELLER_FUNCTION_ID, inParameters, ""));
        }

        return tasks;
    }

    private TaskDTO createMapTask(String reference) {
        List<String> inParameters = new ArrayList<>();
        inParameters.add(reference);

        return new TaskDTO(UUID.randomUUID().toString(), programId, jobId, MAP_FUNCTION_ID, inParameters, "");
    }

    private TaskDTO createReduceTask(String reference) {
        List<String> inParameters = new ArrayList<>();
        inParameters.add(reference);

        return new TaskDTO(UUID.randomUUID().toString(), programId, jobId, REDUCE_FUNCTION_ID, inParameters, "");
    }


    public static void main(String[] args) {

        String programId = UUID.randomUUID().toString();
        String jobId = UUID.randomUUID().toString();

        MapReduceMain application = new MapReduceMain(programId, jobId, 7);

        List<TaskDTO> tasks = application.createTasks(10);

        application.run(tasks);
    }
}
