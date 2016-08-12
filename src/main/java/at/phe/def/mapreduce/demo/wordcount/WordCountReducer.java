package at.phe.def.mapreduce.demo.wordcount;


import at.phe.def.mapreduce.base.ReduceJavaBaseLibraryFunction;
import com.google.gson.JsonPrimitive;

import java.util.Collection;

/**
 * Author: Philip Heimböck
 * Date: 21.04.16.
 */
public class WordCountReducer extends ReduceJavaBaseLibraryFunction<String, Integer, JsonPrimitive, JsonPrimitive> {

    public WordCountReducer() {
        super(String.class, Integer.class);
    }

    @Override
    protected void runReduce(String key, Collection<Integer> integers) {
        int sum = 0;
        for (Integer i : integers) {
            sum += i;
        }

        emit(new JsonPrimitive(key), new JsonPrimitive(sum));
    }
}
