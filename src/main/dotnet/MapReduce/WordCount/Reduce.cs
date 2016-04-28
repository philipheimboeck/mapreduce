using System;
using System.Collections.Generic;
using DefLib;

namespace WordCount
{
	public class Reduce : MonoBaseLibraryFunction
	{
		#region implemented abstract members of MonoBaseLibraryFunction

		public override void Execute()
		{
			// Get tuples
			var tuples = ConvertToObject<List<List<object>>>((string)InParameters[0]);
			
			
			// Count the tuples
			var counter = new Dictionary<string, long?>();
			foreach (var tuple in tuples)
			{
				var v1 = tuple[0] as string;
				var v2 = tuple[1] as long?;
				if (!counter.ContainsKey(v1))
				{
					counter.Add(v1, 0);
				}
				counter[v1] = counter[v1] + v2;
			}

			// Set the result
			Result = counter;
		}

		#endregion
		
	}
}
