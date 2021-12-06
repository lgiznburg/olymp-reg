package ru.rsmu.olympreg.utils;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import ru.rsmu.olympreg.dao.CompetitorDao;
import ru.rsmu.olympreg.entities.GettingResultInfo;
import ru.rsmu.olympreg.entities.ParticipationInfo;
import ru.rsmu.olympreg.services.AssignExamControl;
import ru.rsmu.olympreg.utils.restconnector.TempolwConnector;
import ru.rsmu.olympreg.utils.restconnector.examsapimodel.ExamResult;
import ru.rsmu.olympreg.utils.restconnector.examsapimodel.PersonResult;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author leonid.
 */
public class GetExamResultsJob implements Job {
    private static final int PAGE_SIZE = 100;

    @Inject
    private CompetitorDao competitorDao;

    @Inject
    private TempolwConnector tempolwConnector;

    @Inject
    private AssignExamControl control;


    @Override
    public void execute( JobExecutionContext context ) throws JobExecutionException {
        GettingResultInfo info = null;
        try {
            info = control.getResultQueue().poll( 10, TimeUnit.MILLISECONDS );
        } catch (InterruptedException e) {
            //?
        }

        if ( info == null ){
/*
            List<Long> examIdList = competitorDao.findExamsWithNoResults();
            for ( Long examId : examIdList ) {
                GettingResultInfo nextInfo = new GettingResultInfo( examId, 0 );
                control.getResultQueue().add( nextInfo );
            }
*/
            return;
        }
        ExamResult examResult = tempolwConnector.getExamResult( info.getExamId(), PAGE_SIZE, info.getFirstResult() );
        if ( examResult != null ) {
            for ( PersonResult personResult : examResult.getResults() ) {
                if ( !personResult.getStatus().equalsIgnoreCase( "ожидает проверки" ) ) {
                    List<ParticipationInfo> participationInfoList = competitorDao.findParticipation( info.getExamId(), personResult.getCaseNumber() );
                    for ( ParticipationInfo participationInfo : participationInfoList ) {
                        if ( personResult.getStatus().equalsIgnoreCase( "неявка" ) ) {
                            participationInfo.setResult( -1 );
                        }
                        else if ( personResult.getStatus().equalsIgnoreCase( "завершено" ) ) {
                            participationInfo.setResult( personResult.getFinalScore() );
                        }
                        competitorDao.save( participationInfo );
                    }
                }
            }
            if ( examResult.getMeta().getTotal() > info.getFirstResult() + examResult.getResults().size() ) {
                info.setFirstResult( info.getFirstResult() + examResult.getResults().size() );
                control.getResultQueue().add( info ); // go to next run
            }
        }
    }
}
