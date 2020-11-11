package com.dochi.quartz.step;

import java.util.Date;

import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.Trigger.CompletedExecutionInstruction;
import org.quartz.TriggerListener;

public class TriggerLogListener implements TriggerListener {

    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public void triggerFired(Trigger trigger, JobExecutionContext context) {
        System.out.println(String.format("[%s][%s][triggerFired]", JobLauncher.TIMESTAMP_FMT.format(new Date()), getName()));
    }

    @Override
    public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
        System.out.println(String.format("[%s][%s][vetoJobExecution]", JobLauncher.TIMESTAMP_FMT.format(new Date()), getName()));
        return false;
    }

    @Override
    public void triggerMisfired(Trigger trigger) {
        System.out.println(String.format("[%s][%s][triggerMisfired]", JobLauncher.TIMESTAMP_FMT.format(new Date()), getName()));
    }

    @Override
    public void triggerComplete(Trigger trigger, JobExecutionContext context,
            CompletedExecutionInstruction triggerInstructionCode) {
        System.out.println(String.format("[%s][%s][triggerComplete]", JobLauncher.TIMESTAMP_FMT.format(new Date()), getName()));
    }

}
