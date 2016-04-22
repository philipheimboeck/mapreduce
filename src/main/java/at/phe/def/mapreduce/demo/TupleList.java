package at.phe.def.mapreduce.demo;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Author: Philip Heimböck
 * Date: 22.04.16.
 */
public class TupleList implements Serializable {

    private ArrayList<Tuple<String, Integer>> tuples;

    public TupleList() {
    }

    public TupleList(ArrayList<Tuple<String, Integer>> tuples) {
        this.tuples = tuples;
    }

    public ArrayList<Tuple<String, Integer>> getTuples() {
        return tuples;
    }

    public void setTuples(ArrayList<Tuple<String, Integer>> tuples) {
        this.tuples = tuples;
    }
}
