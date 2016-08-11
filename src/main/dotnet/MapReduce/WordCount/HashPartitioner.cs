using System;
using MapReduceBase;

namespace WordCount
{
	public class HashPartitioner : PartitionerBaseLibrary
	{
		public HashPartitioner () : base(new MapReduceBase.HashPartitioner())
		{
		}
	}
}

