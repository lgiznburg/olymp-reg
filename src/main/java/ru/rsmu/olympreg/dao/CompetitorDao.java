package ru.rsmu.olympreg.dao;

import ru.rsmu.olympreg.entities.CompetitorProfile;
import ru.rsmu.olympreg.entities.OlympiadSubject;
import ru.rsmu.olympreg.entities.ParticipationInfo;
import ru.rsmu.olympreg.entities.User;
import ru.rsmu.olympreg.viewentities.CompetitorFilter;
import ru.rsmu.olympreg.viewentities.SortCriterion;

import java.util.List;

/**
 * @author leonid.
 */
public interface CompetitorDao extends BaseDao {
    int countProfiles( CompetitorFilter filter );

    List<CompetitorProfile> findProfiles( CompetitorFilter filter, List<SortCriterion> toSortCriteria, int startIndex, int size );

    int countSubjectSelectedProfiles();

    List<Object[]> findDetailedStats();

    List<Object[]> findGlobalDetailedStats();

    int countFilledBaseInfo();

    int countUploadCompleted();

    List<ParticipationInfo> findNewParticipations( int classNumber, OlympiadSubject olympiadSubject, int maxResults );

    List<Object[]> findExamsWithNoResults();

    List<ParticipationInfo> findParticipation( long examId, String caseNumber );

    List<CompetitorProfile> findForSecondStage( OlympiadSubject subject, int classNumber, int secondStagePassScore );

    CompetitorProfile findProfile( String personalNumber );

    List<CompetitorProfile> findUncompletedProfiles();

    List<CompetitorProfile> findPreviousYearProfiles();

    boolean isLastYearWinner( User user, OlympiadSubject subject );
}
