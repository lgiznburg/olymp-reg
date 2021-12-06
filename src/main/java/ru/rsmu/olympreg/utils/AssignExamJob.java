package ru.rsmu.olympreg.utils;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import ru.rsmu.olympreg.dao.CompetitorDao;
import ru.rsmu.olympreg.entities.AssignExamInfo;
import ru.rsmu.olympreg.entities.ParticipationInfo;
import ru.rsmu.olympreg.services.AssignExamControl;
import ru.rsmu.olympreg.utils.restconnector.TempolwConnector;
import ru.rsmu.olympreg.utils.restconnector.examsapimodel.PersonInfo;
import ru.rsmu.olympreg.utils.restconnector.examsapimodel.TesteeExamInfo;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author leonid.
 */
public class AssignExamJob  implements Job {
    @Inject
    private CompetitorDao competitorDao;

    @Inject
    private TempolwConnector tempolwConnector;

    @Inject
    private AssignExamControl control;

    @Override
    public void execute( JobExecutionContext context ) throws JobExecutionException {
        AssignExamInfo info = null;
        try {
            info = control.getAssignExamQueue().poll( 2, TimeUnit.SECONDS );
        } catch (InterruptedException e) {
            // log?
        }
        if ( info != null && assignNextPortion( info ) ) {
            control.getAssignExamQueue().offer( info );
        }
    }

    private boolean assignNextPortion( AssignExamInfo info ) {
        List<ParticipationInfo> participationInfos = competitorDao.findNewParticipations( info.getClassNumber(), info.getOlympiadSubject(), 50 );

        List<PersonInfo> personInfos = participationInfos.stream()
                .map( PersonInfo::new ).collect( Collectors.toList());

        List<TesteeExamInfo> examInfos = tempolwConnector.sendPersonToExam( info.getExamExtarnalId(), personInfos );
        for ( TesteeExamInfo examInfo : examInfos ) {
            List<ParticipationInfo> participationResults = participationInfos.stream()
                    .filter( pr -> pr.getProfile().getCaseNumber().equalsIgnoreCase( examInfo.getCaseNumber() ) )
                    .collect( Collectors.toList());
            for ( ParticipationInfo participation: participationResults ) {
                participation.setExamId( info.getExamExtarnalId() );
                participation.setExamName( examInfo.getExamName() );
                participation.setTestingPlanName( examInfo.getTestingPlanName() );
                participation.setDuration( examInfo.getDuration() );
                participation.setStartDate( examInfo.getExamStartTime() );
                participation.setEndDate( examInfo.getExamEndTime() );
                participation.setToken( examInfo.getToken() );
                competitorDao.save( participation );
            }
        }
        return !participationInfos.isEmpty();
    }
}
