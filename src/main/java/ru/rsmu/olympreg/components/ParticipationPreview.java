package ru.rsmu.olympreg.components;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import ru.rsmu.olympreg.dao.SystemPropertyDao;
import ru.rsmu.olympreg.entities.OlympiadConfig;
import ru.rsmu.olympreg.entities.ParticipationInfo;
import ru.rsmu.olympreg.entities.system.StoredPropertyName;

import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author leonid.
 */
public class ParticipationPreview {
    private static final int FIRST_STAGE = 0;

    @Parameter(required = true)
    @Property
    private ParticipationInfo info;

    @Parameter(required = true)
    @Property
    private OlympiadConfig config;

    @Inject
    private Block beforeExam, examInProgress, examFinished, emptyBlock;

    @Inject
    private SystemPropertyDao systemPropertyDao;

    private static final DateFormat defaultDateFormat = new SimpleDateFormat("dd MMMM yyyy года");

    public boolean isExamAssigned() {
        return StringUtils.isNotBlank( info.getExamName() );
    }

    public String getDefaultInfo() {
        String subject = "";
        String examDate = defaultDateFormat.format( info.getStage() == FIRST_STAGE ? config.getFirstStage() : config.getSecondStage() );
        String stage = info.getStage() == FIRST_STAGE ? "Первый" : "Второй";
        switch ( info.getOlympiadSubject() ) {
            case CHEMISTRY:
                subject = "Химии";
                break;
            case BIOLOGY:
                subject = "Биологии";
                break;
        }
        return String.format( "Вы записаны на олимпиаду по <strong>%s</strong>. %s этап пройдет %s.", subject, stage, examDate );
    }

    public String getSubjectName() {
        String subject = "";
        switch ( info.getOlympiadSubject() ) {
            case CHEMISTRY:
                subject = "Химии";
                break;
            case BIOLOGY:
                subject = "Биологии";
                break;
        }
        return subject;
    }

    public String getStageName() {
        return info.getStage() == FIRST_STAGE ? "Первый" : "Второй";
    }

    public String getExamDateFormatted() {
        if ( config == null ) return "";
        return defaultDateFormat.format( info.getStage() == FIRST_STAGE ? config.getFirstStage() : config.getSecondStage() );
    }

    public boolean isDateExpired() {
        if ( config == null ) return true;
        Date examDate = info.getStage() == FIRST_STAGE ? config.getFirstStage() : config.getSecondStage();
        return examDate.before( new Date() );
    }

    public boolean isWarningToShow() {
        return !info.getApproved() || info.getStage() == FIRST_STAGE;
    }

    public Block getInfoBlock() {
        Date now = new Date();
        if ( config == null || info.getStartDate() == null || info.getEndDate() == null ) {
            return emptyBlock;
        }
        else if ( now.before( info.getStartDate() ) ) {
            return beforeExam;
        }
        else if ( now.before( info.getEndDate() ) ) {
            return examInProgress;
        }
        else {
            return examFinished;
        }
    }

    public String getExamLink() {
        try {
            String baseSystemUrl = systemPropertyDao.getProperty( StoredPropertyName.TEMPOLW_SYSTEM_URL );
            URIBuilder uriBuilder = new URIBuilder( baseSystemUrl );
            uriBuilder.setPath( uriBuilder.getPath() + "/directToExam"  );

            uriBuilder.addParameter( "token", info.getToken() );
            return uriBuilder.build().toString();
        } catch (URISyntaxException e) {
            return "";
        }
    }

    public String getResultInfo() {
        if ( info.getResult() == null ) {
            return "<span>Работа находится на проверке. Результаты будут отображены позже.</span>";
        }
        else if( info.getResult() < 0 ) {
            return "<span>Результаты отсутствуют из-за неявки.</span>";
        }
        else {
            return "<div class=\"alert alert-info\"> Ваш результат: <strong>" + String.valueOf( info.getResult() ) + " баллов</strong>.</div>";
        }
    }

    public DateFormat getDateFormat() {
        return new SimpleDateFormat("dd MMM yyyy в HH:mm");
    }
}
