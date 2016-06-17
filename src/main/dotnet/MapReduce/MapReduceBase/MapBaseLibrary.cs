using System;
using DefLib;
using System.Collections.Generic;
using Newtonsoft.Json.Linq;

namespace MapReduceBase
{
	public abstract class MapBaseLibrary<KeyOut, ValueOut> : MonoBaseLibraryFunction
	{
		private JArray MapResult = new JArray();
		private int NumberReducers { get; set; }

		#region implemented abstract members of MonoBaseLibraryFunction
		public override void Execute ()
		{
			// First parameter is the number of partitions to be created
			NumberReducers = ConvertToObject<int>((string)InParameters[0]);

			var input = InParameters.GetRange(1, InParameters.Count - 1);

			// Run the map function
			RunMap(input);

			// Partition the results
			List<JArray> partitions = Partition(MapResult);

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
			var partitioner = new TuplePartitioner(new HashPartitioner(), NumberReducers);
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

		/// <summary>
		/// Adds a new tuple to the result
		/// </summary>
		/// <param name="key">The key of the tuple</param>
		/// <paraReduceResult="value">The value of the tuple</param>
		public void Emit(KeyOut key, ValueOut value) {
			var tuple = new JArray ();
			tuple.Add (key);
			tuple.Add (value);
			MapResult.Add (tuple);
		}

		/// <summary>
		/// Runs the map function
		/// </summary>
		public abstract void RunMap(List<object> parameters);
	}
}

