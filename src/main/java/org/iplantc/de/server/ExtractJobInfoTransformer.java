package org.iplantc.de.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.iplantc.jobs.model.Job;
import org.mule.api.transformer.TransformerException;
import org.mule.config.i18n.MessageFactory;
import org.mule.transformer.AbstractTransformer;

public class ExtractJobInfoTransformer extends AbstractTransformer {

	@SuppressWarnings("unchecked")
	@Override
	protected Object doTransform(Object payload, String encoding)
			throws TransformerException {

	    if (payload instanceof Job) {
	    	return createJobInfo((Job) payload);
	    }
	    else if (payload instanceof Collection<?>) {
	    	Collection<Job> jobs = (Collection<Job>) payload;
			List<JobInfo> jobInfos = new ArrayList<JobInfo>();
			for (Job job : jobs) {
				jobInfos.add(createJobInfo(job));
			}
			return jobInfos;
		} else {
			throw new TransformerException(MessageFactory.createStaticMessage("Received object that was not a Job or list of Jobs"));
		}
	}

	private JobInfo createJobInfo(Job job) {
		JobInfo jobInfo = new JobInfo();
		jobInfo.setId(job.getId().toString());
		jobInfo.setDescription(job.getDescription());
		jobInfo.setState(job.getState().toString());
		jobInfo.setSubmitTime(getTime(job.getSubmitTime()));
		jobInfo.setStartTime(getTime(job.getStartTime()));
		jobInfo.setEndTime(getTime(job.getEndTime()));
		return jobInfo;
	}

	private String getTime(Date time) {
		if (time != null) {
			return Long.toString(time.getTime());
		}
		return null;
	}
}
