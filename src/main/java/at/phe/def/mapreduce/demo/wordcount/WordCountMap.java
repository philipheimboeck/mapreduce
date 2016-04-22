package at.phe.def.mapreduce.demo.wordcount;


import at.phe.def.mapreduce.demo.JavaBaseLibraryFunction;
import at.phe.def.mapreduce.demo.Tuple;
import at.phe.def.mapreduce.demo.TupleList;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Philip Heimböck
 * Date: 21.04.16.
 */
public class WordCountMap extends JavaBaseLibraryFunction {

    @Override
    public void run(List<String> parameters) throws Exception {
        String text = parameters.get(0);

        ArrayList<Tuple<String, Integer>> words = new ArrayList<>();
        for (String word : text.split(" ")) {
            words.add(new Tuple<>(word, 1));
        }

        setResult(new TupleList(words));
    }
}
