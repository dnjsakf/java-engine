package com.dochi.labs.sch.listener;

import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.Trigger.CompletedExecutionInstruction;
import org.quartz.TriggerListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyTriggerListener implements TriggerListener {

    private final Logger LOGGER = LoggerFactory.getLogger(MyTriggerListener.class);

    public String getName() {
        return MyTriggerListener.class.getName();
    }

    public void triggerFired(Trigger trigger, JobExecutionContext context) {
    	LOGGER.info("{} trigger is fired, next: {}", trigger.getKey());
    }

    public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
        LOGGER.info("{} was about to be executed but a TriggerListener vetoed it's execution", context.getJobDetail().getKey().toString());
        return false;
    }

    public void triggerMisfired(Trigger trigger) {
        LOGGER.info("{} trigger was misfired", trigger.getKey());
    }

    public void triggerComplete(Trigger trigger, JobExecutionContext context, CompletedExecutionInstruction triggerInstructionCode) {
        LOGGER.info("{} trigger is complete", trigger.getKey());
    }
}