using System;
using DefLib;
using System.Collections.Generic;
using Newtonsoft.Json.Linq;

namespace MapReduceBase
{
	public abstract class MapBaseLibrary<KeyOut, ValueOut> : MonoBaseLibraryFunction
	{
		private JArray MapResult = new JArray();

		#region implemented abstract members of MonoBaseLibraryFunction
		public override void Execute ()
		{
			// Run the map function
			RunMap(InParameters);

			// Save the result
			Result = MapResult;
		}
		#endregion

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

