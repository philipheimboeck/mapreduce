package at.phe.def.mapreduce.partitioner;

/**
 * Author: Philip Heimböck
 * Date: 28.04.16.
 */
public class HashPartitioner implements IPartitioner {

    @Override
    public int partition(Object key, int nrPartitions) {
        int hash = Math.abs(key.hashCode());

        return hash % nrPartitions;
    }
}
