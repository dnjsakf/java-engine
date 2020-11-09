package com.dochi.labs;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.Trigger.CompletedExecutionInstruction;
import org.quartz.TriggerListener;

public class MyTriggerListener implements TriggerListener {

    public static final String NUM_EXECUTIONS = "NumExecutions";

    public String getName() {
        return MyTriggerListener.class.getName();
    }

    public void triggerFired(Trigger trigger, JobExecutionContext context) {
        System.out.println(String.format("[1][%s]", trigger.getKey().toString()));
    }

    public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
        JobDataMap map = context.getJobDetail().getJobDataMap();
        int executeCount = -1;
        if (map.containsKey(NUM_EXECUTIONS)) {
            executeCount = map.getInt(NUM_EXECUTIONS);
        }
        System.out.println(String.format("[2][%s][%d]", trigger.getKey().toString(), executeCount));
        
        return executeCount > 5;
    }

    public void triggerMisfired(Trigger trigger) {
        System.out.println(String.format("[3][%s]", trigger.getKey().toString()));
    }

    public void triggerComplete(Trigger trigger, JobExecutionContext context,
            CompletedExecutionInstruction triggerInstructionCode) {
        System.out.println(String.format("[5][%s]", trigger.getKey().toString()));
    }
}