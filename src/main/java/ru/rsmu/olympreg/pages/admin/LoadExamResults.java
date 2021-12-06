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
import ru.rsmu.olympreg.entities.GettingResultInfo;
import ru.rsmu.olympreg.services.AssignExamControl;
import ru.rsmu.olympreg.utils.restconnector.TempolwConnector;
import ru.rsmu.olympreg.utils.restconnector.examsapimodel.ExamFacade;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author leonid.
 */
public class LoadExamResults {
    @Inject
    private TempolwConnector tempolwConnector;

    @Property
    private ExamStatistics examStatistics;  // to interact through stats list

    @Property
    private List<ExamStatistics> overallStats;

    @Property
    private ExamStatistics exam; // to store form selection

    @Inject
    private AssignExamControl control;

    @Inject
    private CompetitorDao competitorDao;

    @InjectComponent
    private Form assignExam;

    @InjectComponent
    private Field examSelect;


    public void onActivate() {
        List<Object[]> examIdList = competitorDao.findExamsWithNoResults();

        List<ExamFacade> examInfoList = tempolwConnector.getAllExams();

        overallStats = examIdList.stream().map( ExamStatistics::new ).collect( Collectors.toList());
        overallStats.forEach( st -> examInfoList.stream()
                .filter( ei -> ei.getId() == st.examId )
                .findFirst().ifPresent( exam -> {
                    st.setExamDate( exam.getDate() );
                    st.setExamName( exam.getName() );
                    st.setPlanName( exam.getEventType() );
                    st.setSubject( exam.getSubject() );
                } ) );
    }

    public static class ExamResultSelectModel extends AbstractSelectModel {
        List<ExamStatistics> statistics;

        public ExamResultSelectModel( List<ExamStatistics> statisticsList ) {
            this.statistics = statisticsList;
        }

        @Override
        public List<OptionGroupModel> getOptionGroups() {
            return null;
        }

        @Override
        public List<OptionModel> getOptions() {
            List<OptionModel> options = new ArrayList<OptionModel>();
            for (ExamStatistics stat : statistics ) {
                options.add(new OptionModelImpl( stat.getExamName() + ": " +stat.getSubject() + " (" + stat.getPlanName() + ")", stat));
            }
            return options;
        }
    }

    public SelectModel getExamSelectModel() {
        return new ExamResultSelectModel( overallStats );
    }

    public ValueEncoder<ExamStatistics> getExamResultValueEncoder() {
        return new ValueEncoder<ExamStatistics>() {
            @Override
            public String toClient( ExamStatistics examFacade ) {
                return String.valueOf( examFacade.getExamId() );
            }

            @Override
            public ExamStatistics toValue( String s ) {
                long id = Long.parseLong( s );
                return overallStats.stream().filter( ex -> ex.getExamId() == id )
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
        if ( exam != null ) {
            GettingResultInfo nextInfo = new GettingResultInfo( exam.examId, 0 );
            control.getResultQueue().add( nextInfo );
        }
    }

    public static class ExamStatistics {
        private String examName;
        private String subject;
        private String planName;
        private Date examDate;
        private Long examId;
        private int count;

        public ExamStatistics() {
        }

        public ExamStatistics( Object[] income ) {
            this.examId = (Long) income[0];
            this.count = ((Long)income[1]).intValue();
        }

        public String getExamName() {
            return examName;
        }

        public void setExamName( String examName ) {
            this.examName = examName;
        }

        public Date getExamDate() {
            return examDate;
        }

        public void setExamDate( Date examDate ) {
            this.examDate = examDate;
        }

        public Long getExamId() {
            return examId;
        }

        public void setExamId( Long examId ) {
            this.examId = examId;
        }

        public int getCount() {
            return count;
        }

        public void setCount( int count ) {
            this.count = count;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject( String subject ) {
            this.subject = subject;
        }

        public String getPlanName() {
            return planName;
        }

        public void setPlanName( String planName ) {
            this.planName = planName;
        }

        public String getFullName() {
            return examName + ": " + subject + " (" + planName + ")";
        }
    }
}
