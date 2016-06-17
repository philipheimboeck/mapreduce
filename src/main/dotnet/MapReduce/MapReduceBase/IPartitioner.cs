using System;

namespace MapReduceBase
{
	public interface IPartitioner
	{
		int Partition(Object key, int nrPartitions);
	}
}

