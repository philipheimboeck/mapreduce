package at.phe.def.mapreduce.demo;

import java.io.Serializable;

/**
 * Author: Philip Heimböck
 * Date: 21.04.16.
 */
public class Tuple<T, T1> implements Serializable {
    public T value1;
    public T1 value2;

    public Tuple() {
    }

    public Tuple(T value1, T1 value2) {
        this.value1 = value1;
        this.value2 = value2;
    }
}
