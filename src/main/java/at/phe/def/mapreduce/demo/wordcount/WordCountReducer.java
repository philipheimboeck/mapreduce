package at.phe.def.mapreduce.demo.wordcount;


import at.enfilo.def.prototype1.commons.DEFTypeConverter;
import at.phe.def.mapreduce.demo.JavaBaseLibraryFunction;
import at.phe.def.mapreduce.demo.Tuple;
import at.phe.def.mapreduce.demo.TupleList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Author: Philip Heimböck
 * Date: 21.04.16.
 */
public class WordCountReducer extends JavaBaseLibraryFunction {

    @Override
    public void run(List<String> parameters) throws Exception {
        TupleList text = DEFTypeConverter.convert(inParameters.get(0), TupleList.class);

        HashMap<String, Integer> words = new HashMap<>();
        for (Tuple<String, Integer> tuple : text.getTuples()) {
            if(!words.containsKey(tuple.value1)) {
                words.put(tuple.value1, tuple.value2);
            } else {
                words.put(tuple.value1, words.get(tuple.value1) + tuple.value2);
            }
        }

        setResult(words);
    }
}
