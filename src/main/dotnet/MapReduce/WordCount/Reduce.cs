using System;
using System.Collections.Generic;
using MapReduceBase;

namespace WordCount
{
	public class Reduce : ReduceBaseLibrary<string, int, string, int>
	{
		#region implemented abstract members of MonoBaseLibraryFunction

		public override void RunReduce(string key, ICollection<int> values)
		{
			// Count the tuples
			int result = 0;
			foreach (var value in values)
			{
				result += value;
			}

			// Set the result
			Emit(key, result);
		}

		#endregion
		
	}
}
