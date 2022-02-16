package ru.rsmu.olympreg.services;

import org.quartz.Job;

/**
 * @author leonid.
 */
public class RunJobDescription {
    private final Class<? extends Job> jobClass;

    private final int minuteInterval;

    public RunJobDescription( Class<? extends Job> jobClass, int minuteInterval ) {
        this.jobClass = jobClass;
        this.minuteInterval = minuteInterval;
    }

    public Class<? extends Job> getJobClass() {
        return jobClass;
    }

    public int getMinuteInterval() {
        return minuteInterval;
    }
}
