package at.phe.def.mapreduce.demo;

import at.phe.def.mapreduce.base.PartitionerBaseLibraryFunction;
import at.phe.def.mapreduce.partitioner.HashPartitioner;

/**
 * Author: Philip Heimböck
 * Date: 11.08.16.
 */
public class HashPartitionerLibraryFunction extends PartitionerBaseLibraryFunction {
    public HashPartitionerLibraryFunction() {
        super(new HashPartitioner());
    }
}