package ru.rsmu.olympreg.services.impl;

import org.apache.tapestry5.ioc.annotations.Inject;
import ru.rsmu.olympreg.dao.CompetitorDao;
import ru.rsmu.olympreg.dao.EmailDao;
import ru.rsmu.olympreg.dao.SystemPropertyDao;
import ru.rsmu.olympreg.services.CompetitorNotificationService;
import ru.rsmu.olympreg.utils.SendPreviousYearReminderTask;
import ru.rsmu.olympreg.utils.SendUncompletedNotificationTask;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author leonid.
 */
public class CompetitorNotificationServiceImpl implements CompetitorNotificationService {

    @Inject
    private CompetitorDao competitorDao;

    @Inject
    private EmailDao emailDao;

    @Inject
    private SystemPropertyDao systemPropertyDao;

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    private Future<?> status;

    @Override
    public void notifyUncompletedCompetitors() {
        if ( isDone() ) {
            status = executorService.submit( new SendUncompletedNotificationTask( emailDao, competitorDao ) );
        }
    }

    @Override
    public void notifyPreviousYearCompetitors() {
        if ( isDone() ) {
            status = executorService.submit( new SendPreviousYearReminderTask( emailDao, competitorDao, systemPropertyDao ) );
        }
    }

    @Override
    public boolean isDone() {
        return status == null || status.isDone();
    }
}
