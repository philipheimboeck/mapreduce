using System;
using MapReduceBase;

namespace WordCount
{
	public class Map : MapBaseLibrary<string, int>
	{
		#region implemented abstract members of MapBaseLibrary

		public override void RunMap (System.Collections.Generic.List<object> parameters)
		{
			var text = parameters[0] as string;

			foreach (var word in text.Split(' ')) {
				Emit(word.ToLower(), 1);
			}
		}

		#endregion
	}
}

