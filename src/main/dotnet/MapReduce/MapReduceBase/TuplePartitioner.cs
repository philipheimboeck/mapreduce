using System;
using System.Collections.Generic;
using Newtonsoft.Json.Linq;

namespace MapReduceBase
{
	public class TuplePartitioner
	{
		public IPartitioner Partitioner 
		{
			get;
			set;
		}

		protected int NumberReducers {
			get;
			set;
		}

		protected List<JArray> partitions = new List<JArray>();

		public TuplePartitioner(IPartitioner partitioner, int numberReducers) {
			Partitioner = partitioner;
			NumberReducers = numberReducers;

			for (int i = 0; i < NumberReducers; i++) {
				partitions.Add(new JArray());
			}
		}

		public void partition(JArray input) {
			// Partition the input
			foreach (var tuple in input) {
				int chosenReducer = Partitioner.Partition (tuple [0], NumberReducers);
				partitions [chosenReducer].Add (tuple);
			}
		}

		public List<JArray> getPartitions() {
			return partitions;
		}
	}
}

