package ru.rsmu.olympreg.services.impl;

import org.apache.tapestry5.ioc.LoggerSource;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.quartz.*;
import ru.rsmu.olympreg.services.RunJobDescription;
import ru.rsmu.olympreg.services.RunJobsService;
import ru.rsmu.olympreg.utils.AssignExamJob;
import ru.rsmu.olympreg.utils.CleanUpRegistrationJob;
import ru.rsmu.olympreg.utils.GetExamResultsJob;
import ru.rsmu.olympreg.utils.SendEmailJob;

import java.util.*;

/**
 * @author leonid.
 */
public class RunJobsServiceImpl implements RunJobsService {

    Scheduler scheduler;

    LoggerSource loggerSource;

    private final List<RunJobDescription> jobList;

    public RunJobsServiceImpl( Collection<RunJobDescription> configuration, Scheduler scheduler, LoggerSource loggerSource ) {
        this.scheduler = scheduler;
        this.loggerSource = loggerSource;
        jobList = new ArrayList<>( configuration );
    }

    public void starUp() {
        for ( RunJobDescription jobClass : jobList ) {
            JobDetail jobDescription = JobBuilder.newJob( jobClass.getJobClass() ).build();

            ScheduleBuilder<?> scheduleBuilder = SimpleScheduleBuilder
                    .repeatMinutelyForever( jobClass.getMinuteInterval() );
            Trigger jobTrigger = TriggerBuilder.newTrigger()
                    .withSchedule( scheduleBuilder )
                    .forJob( jobDescription )
                    .build();

            try {
                scheduler.scheduleJob( jobDescription, jobTrigger );
            } catch (SchedulerException e) {
                loggerSource.getLogger( RunJobsServiceImpl.class )
                        .error( "Can't start scheduled jobs " + jobClass.getJobClass().getSimpleName(), e );
            }
        }
    }
}
