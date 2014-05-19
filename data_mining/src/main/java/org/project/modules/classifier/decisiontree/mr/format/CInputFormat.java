package org.project.modules.classifier.decisiontree.mr.format;

import java.io.IOException;

import org.apache.hadoop.mapred.InputFormat;
import org.apache.hadoop.mapred.InputSplit;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.RecordReader;
import org.apache.hadoop.mapred.Reporter;

@SuppressWarnings("rawtypes")
public class CInputFormat implements InputFormat {

	@Override
	public RecordReader getRecordReader(InputSplit inputSplit, JobConf job,
			Reporter reporter) throws IOException {
		return null;
	}

	@Override
	public InputSplit[] getSplits(JobConf job, int num) throws IOException {
		return null;
	}

}
