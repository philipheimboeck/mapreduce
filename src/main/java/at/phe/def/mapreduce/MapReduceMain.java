package at.phe.def.mapreduce;

import at.enfilo.def.prototype1.commons.PersistenceHandler;
import at.enfilo.def.prototype1.commons.PersistenceHandlerFactory;
import at.enfilo.def.prototype1.commons.exceptions.ResourceAccessException;
import at.enfilo.def.prototype1.commons.exceptions.ResourceNotExistsException;
import at.enfilo.def.prototype1.commons.remote.TaskDTO;

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
    private void run(List<TaskDTO> tasks, int numberReducers) {
        Collection<Callable<List<String[]>>> appTaskCallables = new ArrayList<>();
        Collection<Callable<ReduceTaskDTO>> reducerCallables = new ArrayList<>();

        // Provide a list with runnable StoryTellers
        tasks.forEach(appTask -> appTaskCallables.add(new MapTaskCallable(createMapTaskDTO(appTask, numberReducers))));

        try {
            // Run all app tasks and mappers and get the references of their output
            final Collection<Future<List<String[]>>> partitionReferences = executorService.invokeAll(appTaskCallables);

            // Create reducers
            for (int i = 0; i < numberReducers; i++) {
                reducerCallables.add(new ReduceTaskCallable(createReduceTaskDTO(partitionReferences, i)));
            }

            // Start the reducers
            Collection<Future<ReduceTaskDTO>> reduceTaskDTOs = executorService.invokeAll(reducerCallables);

            // Print the result
            printResult(reduceTaskDTOs);

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

    private void printResult(Collection<Future<ReduceTaskDTO>> taskDTOs) {
        taskDTOs.forEach(task -> {
            try {
                System.out.println(persistenceHandler.readResult(programId, jobId, task.get().task.getId()));
            } catch (ResourceAccessException | ResourceNotExistsException | InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
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

    private MapTaskDTO createMapTaskDTO(TaskDTO appTask, int numberReducers) {
        return new MapTaskDTO(appTask, createMapTask(UUID.randomUUID().toString()), numberReducers);
    }

    private TaskDTO createReduceTask(String reference) {
        List<String> inParameters = new ArrayList<>();
        inParameters.add(reference);

        return new TaskDTO(UUID.randomUUID().toString(), programId, jobId, REDUCE_FUNCTION_ID, inParameters, "");
    }

    private ReduceTaskDTO createReduceTaskDTO(Collection<Future<List<String[]>>> partitionReferences, int partitionNumber) {
        List<String[]> taskReferences = new ArrayList<>();

        // Get all references for this reduce task
        for (Future<List<String[]>> partitions : partitionReferences) {
            try {
                taskReferences.add(partitions.get().get(partitionNumber));
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        return new ReduceTaskDTO(createReduceTask(UUID.randomUUID().toString()), taskReferences);
    }


    public static void main(String[] args) {

        String programId = UUID.randomUUID().toString();
        String jobId = UUID.randomUUID().toString();

        MapReduceMain application = new MapReduceMain(programId, jobId, 7);

        // Create 10 StoryTellers
        List<TaskDTO> tasks = application.createTasks(10);

        // Start the application with 3 reducers
        application.run(tasks, 3);
    }
}
