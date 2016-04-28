using NUnit.Framework;
using System;
using System.Collections.Generic;

using WordCount;

namespace MapReduceTest
{
	[TestFixture()]
	public class Test
	{
		[Test()]
		public void TestCase()
		{
			var parameters = new List<string>();
			var words = "[[\"a\", 1],[\"word\", 1],[\"and\", 1],[\"another\", 1],[\"word\", 1],[\"in\", 1],[\"a\", 1],[\"sentence\", 1]]";
			parameters.Add(words);

			var reduce = new Reduce();
			reduce.SetInParameters(parameters);
			reduce.pHandler = null; // Disable the persistence handler

			reduce.Execute();

			var result = reduce.Result as Dictionary<string, long?>;
			Assert.True(result.ContainsKey("a"));
			Assert.AreEqual(2, result["a"]);
			Assert.True(result.ContainsKey("word"));
			Assert.AreEqual(2, result["word"]);
			Assert.True(result.ContainsKey("and"));
			Assert.AreEqual(1, result["and"]);
			Assert.True(result.ContainsKey("another"));
			Assert.AreEqual(1, result["another"]);
			Assert.True(result.ContainsKey("in"));
			Assert.AreEqual(1, result["in"]);
			Assert.True(result.ContainsKey("sentence"));
			Assert.AreEqual(1, result["sentence"]);

			// Something that doesn occurr
			Assert.False(result.ContainsKey("jesus"));
		}
	}
}

