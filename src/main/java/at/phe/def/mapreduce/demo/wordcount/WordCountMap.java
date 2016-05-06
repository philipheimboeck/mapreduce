package at.phe.def.mapreduce.demo.wordcount;


import at.phe.def.mapreduce.base.MapJavaBaseLibraryFunction;
import at.phe.def.mapreduce.demo.JavaBaseLibraryFunction;
import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;

import java.util.List;

/**
 * Author: Philip Heimböck
 * Date: 21.04.16.
 */
public class WordCountMap extends MapJavaBaseLibraryFunction<JsonPrimitive, JsonPrimitive> {

    @Override
    protected void runMap(List<String> parameters) {
        String text = parameters.get(0);

        for (String word : text.split(" ")) {
            emit(new JsonPrimitive(word.toLowerCase()), new JsonPrimitive(1));
        }
    }
}
