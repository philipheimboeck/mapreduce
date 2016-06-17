using NUnit.Framework;
using System;
using System.Collections.Generic;

using WordCount;
using Newtonsoft.Json.Linq;

namespace MapReduceTest
{
	[TestFixture()]
	public class Test
	{
		[Test()]
		public void ReduceTestCase()
		{
			var parameters = new List<string>();
			var partition1 = "[[\"a\", 1],[\"word\", 1],[\"and\", 1],[\"another\", 1],[\"word\", 1],[\"in\", 1],[\"a\", 1],[\"sentence\", 1]]";
			var partition2 = "[[\"a\", 1],[\"word\", 1],[\"and\", 1],[\"another\", 1],[\"in\", 1],[\"a\", 1],[\"sentence\", 1]]";
			parameters.Add(partition1);
			parameters.Add(partition2);

			var reduce = new Reduce ();
			reduce.SetInParameters(parameters);
			reduce.pHandler = null; // Disable the persistence handler

			reduce.Execute();

			var result = reduce.Result as Dictionary<string, int>;
			Assert.True(result.ContainsKey("a"));
			Assert.AreEqual(4, result["a"]);
			Assert.True(result.ContainsKey("word"));
			Assert.AreEqual(3, result["word"]);
			Assert.True(result.ContainsKey("and"));
			Assert.AreEqual(2, result["and"]);
			Assert.True(result.ContainsKey("another"));
			Assert.AreEqual(2, result["another"]);
			Assert.True(result.ContainsKey("in"));
			Assert.AreEqual(2, result["in"]);
			Assert.True(result.ContainsKey("sentence"));
			Assert.AreEqual(2, result["sentence"]);

			// Something that doesn occurr
			Assert.False(result.ContainsKey("jesus"));
		}
			
		[Test()]
		public void MapTestCase()
		{
			var parameters = new List<string>();
			var partitions = "3";
			var text = "This is just some random text with some words that occur two times";
			parameters.Add (partitions);
			parameters.Add (text);

			var map = new Map ();
			map.SetInParameters(parameters);
			map.pHandler = new VolatilePersistenceHandler ();

			map.Execute();

			var result = map.Result as List<string>;

			// Assert that there are three partitions
			Assert.AreEqual(3, result.Count);

			// Assert that all words are contained in the map
			var p1 = map.pHandler.ReadResource(map.ProgramId, map.JobId, result[0]);
			var p2 = map.pHandler.ReadResource(map.ProgramId, map.JobId, result[1]);
			var p3 = map.pHandler.ReadResource(map.ProgramId, map.JobId, result[2]);

			var merged = p1 + p2 + p3;
			foreach(var word in text.Split(' ')) {
				Assert.True (merged.Contains (word.ToLower()), "Word " + word.ToLower() + " not found in " + merged);
			}
		}
	}
}

