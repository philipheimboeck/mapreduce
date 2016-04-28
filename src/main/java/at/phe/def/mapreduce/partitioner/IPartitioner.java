package at.phe.def.mapreduce.partitioner;

/**
 * Author: Philip Heimböck
 * Date: 28.04.16.
 * <p>
 * Partitioner decides what reducer gets what input
 */
public interface IPartitioner {

    /**
     * @param key The key of the object to partition
     * @param nrPartitions The number of partitions
     * @return the chosen partition (beginning with 0)
     */
    int partition(Object key, int nrPartitions);
}
