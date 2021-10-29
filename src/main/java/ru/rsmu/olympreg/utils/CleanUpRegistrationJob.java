package ru.rsmu.olympreg.utils;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import ru.rsmu.olympreg.dao.UserDao;
import ru.rsmu.olympreg.entities.UserCandidate;

import java.util.Calendar;
import java.util.List;

/**
 * @author leonid.
 */
public class CleanUpRegistrationJob implements Job {
    @Inject
    private UserDao userDao;

    @Override
    public void execute( JobExecutionContext context ) throws JobExecutionException {
        Calendar calendar = Calendar.getInstance();
        calendar.add( Calendar.DAY_OF_YEAR, -1 );
        List<UserCandidate> candidates = userDao.findOutdatedCandidates( calendar.getTime() );
        for ( UserCandidate candidate : candidates ) {
            userDao.delete( candidate );
        }
    }
}
