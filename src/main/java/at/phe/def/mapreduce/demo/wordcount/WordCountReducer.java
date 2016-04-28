package at.phe.def.mapreduce.demo.wordcount;


import at.enfilo.def.prototype1.commons.DEFTypeConverter;
import at.phe.def.mapreduce.demo.JavaBaseLibraryFunction;
import com.google.gson.JsonArray;

import java.util.HashMap;
import java.util.List;

/**
 * Author: Philip Heimböck
 * Date: 21.04.16.
 */
public class WordCountReducer extends JavaBaseLibraryFunction {

    @Override
    public void run(List<String> parameters) throws Exception {
        JsonArray words = DEFTypeConverter.convert(inParameters.get(0), JsonArray.class);

        HashMap<String, Integer> result = new HashMap<>();

        words.forEach(data -> {
                    JsonArray tuple = data.getAsJsonArray();

                    if (!result.containsKey(tuple.get(0).getAsString())) {
                        result.put(tuple.get(0).getAsString(), 0);
                    }

                    result.put(tuple.get(0).getAsString(), result.get(tuple.get(0).getAsString()) + tuple.get(1).getAsInt());
                }
        );

        setResult(result);
    }
}
