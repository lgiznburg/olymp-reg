package ru.rsmu.olympreg.pages.admin;

import org.apache.tapestry5.*;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.EventLink;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.internal.OptionModelImpl;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.apache.tapestry5.util.AbstractSelectModel;
import ru.rsmu.olympreg.dao.CompetitorDao;
import ru.rsmu.olympreg.dao.SystemPropertyDao;
import ru.rsmu.olympreg.entities.AssignExamInfo;
import ru.rsmu.olympreg.entities.OlympiadSubject;
import ru.rsmu.olympreg.entities.system.StoredPropertyName;
import ru.rsmu.olympreg.pages.control.Statistics;
import ru.rsmu.olympreg.services.AssignExamControl;
import ru.rsmu.olympreg.utils.restconnector.TempolwConnector;
import ru.rsmu.olympreg.utils.restconnector.examsapimodel.ExamFacade;

import java.util.ArrayList;
import java.util.List;

/**
 * @author leonid.
 */
public class AssignExam {

    @Inject
    private TempolwConnector tempolwConnector;

    @Property
    private List<ExamFacade> exams;

    @Property
    private ExamFacade exam;

    @Property
    private int classNumber;

    @Property
    private OlympiadSubject olympiadSubject;

    @Property
    private OlympiadSubject olympiadSubjectInternal;

    private List<Statistics.SubjectStats> overallStats;

    @Inject
    private AssignExamControl control;

    @Inject
    private CompetitorDao competitorDao;

    @Inject
    private SystemPropertyDao systemPropertyDao;

    @InjectComponent
    private Form assignExam;

    @InjectComponent
    private Field examSelect;

    @InjectComponent
    private EventLink refreshStatsZone;

    @InjectComponent
    private Zone statsZone;

    @Inject
    private AjaxResponseRenderer ajaxResponseRenderer;

    @Inject
    private JavaScriptSupport javaScriptSupport;

    @Inject
    private Request request;

    // The code

    public void afterRender() {
        String eventURL = refreshStatsZone.getLink().toAbsoluteURI();
        String serverUrl = systemPropertyDao.getProperty( StoredPropertyName.MY_OWN_URI );
        String eventSchema = eventURL.split( "://" )[0];
        String serverSchema = serverUrl.split( "://" )[0];
        if ( !eventSchema.equalsIgnoreCase( serverSchema ) ) {
            eventURL = eventURL.replace( eventSchema, serverSchema );
        }

        javaScriptSupport.require("zone-updater").with( statsZone.getClientId(), eventURL, 30 );
    }

    void onRefreshStatsZone() {
        if (request.isXHR()) {
            calculateOverallStats();
            ajaxResponseRenderer.addRender( statsZone );
        }
    }

    public void onActivate() {
        exams = tempolwConnector.getUpcomingExams();
        calculateOverallStats();
    }


    public static class ExamSelectModel extends AbstractSelectModel {
        List<ExamFacade> examFacades;

        public ExamSelectModel( List<ExamFacade> examFacades ) {
            this.examFacades = examFacades;
        }

        @Override
        public List<OptionGroupModel> getOptionGroups() {
            return null;
        }

        @Override
        public List<OptionModel> getOptions() {
            List<OptionModel> options = new ArrayList<OptionModel>();
            for (ExamFacade examFacade : examFacades) {
                options.add(new OptionModelImpl( examFacade.getName() + ": " +examFacade.getSubject() + " (" + examFacade.getEventType() + ")", examFacade));
            }
            return options;
        }
    }

    public SelectModel getExamSelectModel() {
        return new ExamSelectModel( exams );
    }

    public ValueEncoder<ExamFacade> getExamValueEncoder() {
        return new ValueEncoder<ExamFacade>() {
            @Override
            public String toClient( ExamFacade examFacade ) {
                return String.valueOf( examFacade.getId() );
            }

            @Override
            public ExamFacade toValue( String s ) {
                long id = Long.parseLong( s );
                return exams.stream().filter( ex -> ex.getId() == id )
                        .findAny().orElse( null );
            }
        };
    }

    public void onValidateFromAssignExam() {
        if ( !control.getResultQueue().isEmpty() ) {
            assignExam.recordError( examSelect, "Процесс уже запущен, дождитесь окончания" );
        }
    }

    public void onSuccessFromAssignExam() {
        if ( classNumber != 0 && exam != null && olympiadSubject != null ) {
            AssignExamInfo info = new AssignExamInfo();
            info.setExamExtarnalId( exam.getId() );
            info.setOlympiadSubject( olympiadSubject );
            info.setClassNumber( classNumber );

            if ( ! control.getAssignExamQueue().offer( info ) ) {
                //write error
                assignExam.recordError( "Не удалось запустить" );
            }
        }
    }

    private void calculateOverallStats() {
        overallStats = new ArrayList<>();
        List<Object[]> rawResult = competitorDao.findGlobalDetailedStats();
        for ( Object[] row : rawResult ) {
            Statistics.SubjectStats stats = new Statistics.SubjectStats( (Integer)row[0], (OlympiadSubject) row[1], ((Long)row[2]).intValue() );
            overallStats.add( stats );
        }
    }

    public int getOverall10() {
        Statistics.SubjectStats stats = findStatsForClassAndSubject( overallStats, olympiadSubjectInternal, 10 );
        if ( stats != null ) {
            return stats.getCount();
        }
        return 0;
    }


    public int getOverall11() {
        Statistics.SubjectStats stats = findStatsForClassAndSubject( overallStats, olympiadSubjectInternal, 11 );
        if ( stats != null ) {
            return stats.getCount();
        }
        return 0;
    }

    private Statistics.SubjectStats findStatsForClassAndSubject( List<Statistics.SubjectStats> statsList, OlympiadSubject olympiad, int classNumber ) {
        return statsList.stream()
                .filter( st -> st.getSubject() == olympiad && st.getClassNumber() == classNumber )
                .findAny().orElse( null );
    }

    public String getOlympiadSubjectName() {
        switch ( olympiadSubjectInternal ) {
            case CHEMISTRY:
                return "Химия";
            case BIOLOGY:
                return "Биология";
            default:
                return "";
        }
    }

    public OlympiadSubject[] getAllOlympiadSubject() {
        return OlympiadSubject.values();
    }
}
