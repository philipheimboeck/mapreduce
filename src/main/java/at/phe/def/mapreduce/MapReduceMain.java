package at.phe.def.mapreduce;

import at.enfilo.def.prototype1.commons.DEFTypeConverter;
import at.enfilo.def.prototype1.commons.PersistenceHandler;
import at.enfilo.def.prototype1.commons.PersistenceHandlerFactory;
import at.enfilo.def.prototype1.commons.exceptions.ResourceAccessException;
import at.enfilo.def.prototype1.commons.exceptions.ResourceNotExistsException;
import at.enfilo.def.prototype1.commons.remote.TaskDTO;
import at.enfilo.def.prototype1.commons.structs.TaskResult;
import at.enfilo.def.prototype1.workermodule.DispatcherClient;

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

    private static final String STORYTELLER_FUNCTION_ID = "6a2d8863-e96e-4d00-82b7-cc35ae390044";
    private static final String MAP_FUNCTION_ID = "b3499b28-cffa-4668-b974-311e8e767ecc"; // Java
//    private static final String MAP_FUNCTION_ID = "34cd23c4-f473-4010-a26e-69f56e6f3f83"; // Python
    private static final String REDUCE_FUNCTION_ID = "e2497481-a342-40a3-9ece-24e88f5888f1";

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
        List<TaskDTO> mapperTasks = new ArrayList<>();
        List<TaskDTO> reducerTasks = new ArrayList<>();
        Collection<Callable<TaskResult>> storytellerRunnables = new ArrayList<>();
        Collection<Callable<TaskResult>> mapperRunnables = new ArrayList<>();
        Collection<Callable<TaskResult>> reducerRunnables = new ArrayList<>();

        // Provide a list with runnable StoryTellers
        tasks.forEach(taskDTO -> storytellerRunnables.add(() -> dispatcherClient.runTask(taskDTO)));

        try {
            // Run all storytellers
            executorService.invokeAll(storytellerRunnables);

            // Run all mappers

            tasks.forEach(taskDTO -> {
                try {
                    String story = persistenceHandler.readResult(programId, jobId, taskDTO.getId());
                    TaskDTO task = createMapTask(story);
                    mapperTasks.add(task);
                    mapperRunnables.add(() -> dispatcherClient.runTask(task));

                } catch (ResourceAccessException | ResourceNotExistsException e) {
                    e.printStackTrace();
                }
            });

            executorService.invokeAll(mapperRunnables);


            // Shuffle the map output
            Shuffler shuffler = new Shuffler(3);
            mapperTasks.forEach(taskDTO -> {
                try {
                    shuffler.shuffle(taskDTO);
                } catch (ResourceNotExistsException | ResourceAccessException e) {
                    e.printStackTrace();
                }
            });

            shuffler.getShuffled().forEach(shuffledTupleList -> {
                String converted = DEFTypeConverter.convert(shuffledTupleList);
                TaskDTO task = createReduceTask(converted);
                reducerTasks.add(task);
                reducerRunnables.add(() -> dispatcherClient.runTask(task));
            });

            // Start the reducers
            executorService.invokeAll(reducerRunnables);

            reducerTasks.forEach(this::printResult);

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
        inParameters.add("10"); // Number of sentences

        // Create tasks
        for (int i = 0; i < numberTasks; i++) {
            tasks.add(new TaskDTO(UUID.randomUUID().toString(), programId, jobId, STORYTELLER_FUNCTION_ID, inParameters, ""));
        }

        return tasks;
    }

    private TaskDTO createMapTask(String text) {
        List<String> inParameters = new ArrayList<>();
        inParameters.add(text);

        return new TaskDTO(UUID.randomUUID().toString(), programId, jobId, MAP_FUNCTION_ID, inParameters, "");
    }

    private TaskDTO createReduceTask(String tupples) {
        List<String> inParameters = new ArrayList<>();
        inParameters.add(tupples);

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
