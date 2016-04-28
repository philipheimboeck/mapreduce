package at.phe.def.mapreduce.demo.wordcount;


import at.phe.def.mapreduce.demo.JavaBaseLibraryFunction;
import com.google.gson.JsonArray;

import java.util.List;

/**
 * Author: Philip Heimböck
 * Date: 21.04.16.
 */
public class WordCountMap extends JavaBaseLibraryFunction {

    @Override
    public void run(List<String> parameters) throws Exception {
        String text = parameters.get(0);

        JsonArray result = new JsonArray();

        for (String word : text.split(" ")) {
            JsonArray tuple = new JsonArray();
            tuple.add(word);
            tuple.add(1);
            result.add(tuple);
        }

        setResult(result);
    }
}
