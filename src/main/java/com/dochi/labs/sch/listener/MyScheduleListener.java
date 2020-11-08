package com.dochi.labs.sch.listener;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyScheduleListener implements JobListener{
    private final Logger LOGGER = LoggerFactory.getLogger(MyScheduleListener.class);

    public String getName() {
        return MyScheduleListener.class.getName();
    }

    public void jobToBeExecuted(JobExecutionContext context) {
        LOGGER.info("{} is about to be executed", context.getJobDetail().getKey());
    }

    public void jobExecutionVetoed(JobExecutionContext context) {
        LOGGER.info("{} finised execution", context.getJobDetail().getKey());
    }

    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        LOGGER.info("{} was about to be executed but a JobListener vetoed it's execution", context.getJobDetail().getKey());
    }
}
