package ru.rsmu.olympreg.services.impl;

import org.apache.tapestry5.ioc.LoggerSource;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.quartz.*;
import ru.rsmu.olympreg.services.RunJobsService;
import ru.rsmu.olympreg.utils.AssignExamJob;
import ru.rsmu.olympreg.utils.CleanUpRegistrationJob;
import ru.rsmu.olympreg.utils.GetExamResultsJob;
import ru.rsmu.olympreg.utils.SendEmailJob;

/**
 * @author leonid.
 */
public class RunJobsServiceImpl implements RunJobsService {

    @Inject
    Scheduler scheduler;

    @Inject
    LoggerSource loggerSource;

    public RunJobsServiceImpl() {

    }

    public void starUp() {
        JobDetail cleanupJob = JobBuilder.newJob( CleanUpRegistrationJob.class ).build();

        ScheduleBuilder<?> scheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInMinutes( 60 ).repeatForever();
        Trigger cleanupTrigger = TriggerBuilder.newTrigger()
                .withSchedule( scheduleBuilder )
                .forJob( cleanupJob )
                .build();

        JobDetail sendEmailJob = JobBuilder.newJob( SendEmailJob.class ).build();

        ScheduleBuilder<?> emailSchedule = SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInMinutes( 1 ).repeatForever();
        Trigger emailJobTrigger = TriggerBuilder.newTrigger()
                .withSchedule( emailSchedule )
                .forJob( sendEmailJob )
                .build();

        JobDetail assignExamJob = JobBuilder.newJob( AssignExamJob.class ).build();

        ScheduleBuilder<?> assignExamSchedule = SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInMinutes( 2 ).repeatForever();
        Trigger assignJobTrigger = TriggerBuilder.newTrigger()
                .withSchedule( assignExamSchedule )
                .forJob( assignExamJob )
                .build();

        JobDetail gettingResultJob = JobBuilder.newJob( GetExamResultsJob.class ).build();

        ScheduleBuilder<?> gettingResultSchedule = SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInMinutes( 7 ).repeatForever();
        Trigger gettingResultTrigger = TriggerBuilder.newTrigger()
                .withSchedule( gettingResultSchedule )
                .forJob( gettingResultJob )
                .build();

        try {
            scheduler.scheduleJob( cleanupJob, cleanupTrigger );
            scheduler.scheduleJob( sendEmailJob, emailJobTrigger );
            scheduler.scheduleJob( assignExamJob, assignJobTrigger );
            scheduler.scheduleJob( gettingResultJob, gettingResultTrigger );
        } catch (SchedulerException e) {
            loggerSource.getLogger( RunJobsServiceImpl.class ).error( "Can't start scheduled jobs", e );
        }
    }
}
