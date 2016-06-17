using System;
using DefLib;
using System.Collections.Generic;

namespace MapReduceTest
{
	public class VolatilePersistenceHandler : IPersistenceHandler
	{
		private Dictionary<string, string> Storage = new Dictionary<string, string>();

		public VolatilePersistenceHandler ()
		{
		}

		#region IPersistenceHandler implementation

		public void WriteResult (string processId, string jobId, string taskId, string value)
		{
			Storage.Add (processId + jobId + taskId + "RESULT", value);
		}

		public void WriteResource (string processId, string jobId, string key, string value)
		{
			Storage.Add (processId + jobId + key, value);
		}

		public void AppendOutput (string processId, string jobId, string taskId, string value)
		{
			throw new NotImplementedException ();
		}

		public string ReadResource (string processId, string jobId, string resourceName)
		{
			return Storage[processId + jobId + resourceName];
		}

		public string ReadResource (string processId, string resourceName)
		{
			return Storage[processId + resourceName];
		}

		#endregion
	}
}

