using System;

namespace MapReduceBase
{
	public class HashPartitioner : IPartitioner
	{
		#region IPartitioner implementation
		public int Partition (object key, int nrPartitions)
		{
			return Math.Abs (key.GetHashCode()) % nrPartitions;
		}
		#endregion
		
	}
}

