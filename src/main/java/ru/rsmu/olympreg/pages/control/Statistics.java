package ru.rsmu.olympreg.pages.control;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import ru.rsmu.olympreg.dao.CompetitorDao;
import ru.rsmu.olympreg.entities.OlympiadSubject;
import ru.rsmu.olympreg.entities.SubjectRegion;
import ru.rsmu.olympreg.viewentities.CompetitorFilter;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author leonid.
 */
@RequiresRoles( value = {"admin", "manager", "moderator"}, logical = Logical.OR)
public class Statistics {

    @Property
    private SubjectStats stats;

    @Property
    private SubjectRegion region;

    @Property
    private OlympiadSubject olympiadSubject;

    @Inject
    private CompetitorDao competitorDao;

    private Map<SubjectRegion, List<SubjectStats>> statsByRegions;
    private List<SubjectStats> overallStats;

    public void setupRender() {
        statsByRegions = new HashMap<>();
        overallStats = new ArrayList<>();
        List<Object[]> rawResult = competitorDao.findDetailedStats();
        for ( Object[] row : rawResult ) {
            SubjectRegion region = (SubjectRegion) row[0];
            SubjectStats stats = new SubjectStats( (Integer)row[1], (OlympiadSubject) row[2], ((Long)row[3]).intValue() );
            List<SubjectStats> regionList = statsByRegions.computeIfAbsent( region, k -> new ArrayList<>() );
            regionList.add( stats );

            SubjectStats overall = overallStats.stream().filter( st -> st.classNumber == stats.classNumber && st.subject == stats.subject )
                    .findAny().orElse( null );
            if ( overall == null ) {
                overallStats.add( new SubjectStats( stats.classNumber, stats.subject, stats.count) );
            }
            else {
                overall.setCount( overall.count + stats.count );
            }
        }
    }

    public int getTotalAccounts() {
        CompetitorFilter filter = new CompetitorFilter();
        return competitorDao.countProfiles( filter );
    }

    public int getFilledBaseInfo() {
        return competitorDao.countFilledBaseInfo();
    }

    public int getUploadCompleted() {
        return competitorDao.countUploadCompleted();
    }

    public int getSubjectSelected() {
        return competitorDao.countSubjectSelectedProfiles();
    }

    public static class SubjectStats {
        private int classNumber;
        private OlympiadSubject subject;
        private int count;

        public SubjectStats( int classNumber, OlympiadSubject subject, int count ) {
            this.classNumber = classNumber;
            this.subject = subject;
            this.count = count;
        }

        public int getClassNumber() {
            return classNumber;
        }

        public void setClassNumber( int classNumber ) {
            this.classNumber = classNumber;
        }

        public OlympiadSubject getSubject() {
            return subject;
        }

        public void setSubject( OlympiadSubject subject ) {
            this.subject = subject;
        }

        public int getCount() {
            return count;
        }

        public void setCount( int count ) {
            this.count = count;
        }
    }

    public List<SubjectRegion> getFoundRegions() {
        Set<SubjectRegion> regions = new HashSet<>( statsByRegions.keySet() );
        return  regions.stream().sorted( Comparator.comparingInt( SubjectRegion::getCode ) )
                .collect( Collectors.toList() );
    }

    public OlympiadSubject[] getAllOlympiadSubject() {
        return OlympiadSubject.values();
    }

    public int get10Region() {
        List<SubjectStats> statsList = statsByRegions.get( region );
        if ( statsList != null ) {
            SubjectStats stats = findStatsForClassAndSubject( statsList, olympiadSubject, 10 );
            if ( stats != null ) {
                return stats.count;
            }
        }
        return 0;
    }

    public int get11Region() {
        List<SubjectStats> statsList = statsByRegions.get( region );
        if ( statsList != null ) {
            SubjectStats stats = findStatsForClassAndSubject( statsList, olympiadSubject, 11 );
            if ( stats != null ) {
                return stats.count;
            }
        }
        return 0;
    }

    public int get10Overall() {
        SubjectStats stats = findStatsForClassAndSubject( overallStats, olympiadSubject, 10 );
        if ( stats != null ) {
            return stats.count;
        }
        return 0;
    }


    public int get11Overall() {
        SubjectStats stats = findStatsForClassAndSubject( overallStats, olympiadSubject, 11 );
        if ( stats != null ) {
            return stats.count;
        }
        return 0;
    }

    private SubjectStats findStatsForClassAndSubject( List<SubjectStats> statsList, OlympiadSubject olympiad, int classNumber ) {
        return statsList.stream()
                .filter( st -> st.subject == olympiad && st.classNumber == classNumber )
                .findAny().orElse( null );
    }

    public String getOlympiadSubjectName() {
        switch ( olympiadSubject ) {
            case CHEMISTRY:
                return "Химия";
            case BIOLOGY:
                return "Биология";
            default:
                return "";
        }
    }
}
