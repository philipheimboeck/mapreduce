using System;
using DefLib;
using System.Collections.Generic;
using Newtonsoft.Json.Linq;

namespace MapReduceBase
{
	public abstract class PartitionerBaseLibrary : MonoBaseLibraryFunction
	{
		#region implemented abstract members of MonoBaseLibraryFunction

		protected int NumberReducers { get; set; }

		protected IPartitioner Partitioner { get; set; }

		public PartitionerBaseLibrary (IPartitioner partitioner)
		{
			Partitioner = partitioner;
		}

		public override void Execute ()
		{
			// First parameter is the number of partitions to be created
			NumberReducers = ConvertToObject<int>((string)InParameters[0]);

			// Second parameter is the map result
			JArray mapResult = ConvertToObject<JArray>((string)InParameters[1]);

			List<JArray> partitions = Partition(mapResult);

			// Save all partitions
			List<string> references = new List<string>();
			foreach (var partition in partitions) {
				references.Add(WritePartition(partition));
			}

			// Save the references as result
			Result = references;
		}

		#endregion

		/// <summary>
		/// Partition the values
		/// </summary>
		/// <param name="value">Value.</param>
		protected List<JArray> Partition(JArray value) {
			// Partition the result
			var partitioner = new TuplePartitioner(Partitioner, NumberReducers);
			partitioner.partition(value);

			return partitioner.getPartitions();
		}

		/// <summary>
		/// Writes the partition.
		/// </summary>
		/// <returns>The partition reference</returns>
		/// <param name="partition">The partition to write</param>
		protected string WritePartition(JArray partition)
		{
			string partitionKey = System.Guid.NewGuid().ToString();
			WriteJobResource(partition, partitionKey);

			return partitionKey;
		}
	}
}

