using System;
using DefLib;
using System.Collections.Generic;

namespace MapReduceBase
{
	public abstract class ReduceBaseLibrary<KeyIn, ValueIn, KeyOut, ValueOut> : MonoBaseLibraryFunction
	{
		private Dictionary<KeyOut, ValueOut> ReduceResult = new Dictionary<KeyOut, ValueOut>();

		#region implemented abstract members of MonoBaseLibraryFunction
		public override void Execute ()
		{
			// Merge the partitions
			var values = new Dictionary<KeyIn, ICollection<ValueIn>> ();
			foreach(var partition in InParameters) {
				var tuples = ConvertToObject<List<Newtonsoft.Json.Linq.JArray>>((string) partition);
				// Each partition is actually a JSONArray
				foreach (var tuple in tuples) {
					// Every tuple is also a JSONArray
					var key = (KeyIn) tuple[0].ToObject(typeof(KeyIn));
					var value = (ValueIn) tuple[1].ToObject(typeof(ValueIn));

					if (!values.ContainsKey (key)) {
						values.Add (key, new List<ValueIn> ());
					}
					values [key].Add (value);
				}
			}

			foreach (KeyIn key in values.Keys) {
				RunReduce(key, values[key]);
			}

			// Set the result
			Result = ReduceResult;
		}
		#endregion

		/// <summary>
		/// Adds a new tuple to the result
		/// </summary>
		/// <param name="key">The key of the tuple</param>
		/// <paraReduceResult="value">The value of the tuple</param>
		public void Emit(KeyOut key, ValueOut value) {
			ReduceResult.Add (key, value);
		}

		/// <summary>
		/// Runs the reduce function
		/// </summary>
		public abstract void RunReduce(KeyIn key, ICollection<ValueIn> values);
	}
}

