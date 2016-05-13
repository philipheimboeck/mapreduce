package at.phe.def.mapreduce;

import at.enfilo.def.prototype1.commons.DEFTypeConverter;
import at.enfilo.def.prototype1.commons.PersistenceHandlerFactory;
import at.enfilo.def.prototype1.commons.exceptions.ResourceAccessException;
import at.enfilo.def.prototype1.commons.exceptions.ResourceNotExistsException;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Philip Heimböck
 * Date: 12.05.16.
 */
public class PartitionHelper {

    public static List<String> getPartitionReferences(String programId, String jobId, String taskId) throws ResourceNotExistsException, ResourceAccessException {

        // Get the result and return the partition keys
        String mapResult = PersistenceHandlerFactory.getPersistenceHandler()
                .readResult(programId, jobId, taskId);

        return DEFTypeConverter.<ArrayList<String>>convert(mapResult, new TypeToken<ArrayList<String>>(){}.getType());
    }
}
