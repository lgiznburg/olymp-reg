package ru.rsmu.olympreg.components;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import ru.rsmu.olympreg.dao.SystemPropertyDao;
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
    @Parameter(required = true)
    @Property
    private ParticipationInfo info;

    @Inject
    private Block beforeExam, examInProgress, examFinished, emptyBlock;

    @Inject
    private SystemPropertyDao systemPropertyDao;

    public boolean isExamAssigned() {
        return StringUtils.isNotBlank( info.getExamName() );
    }

    public String getDefaultInfo() {
        switch ( info.getOlympiadSubject() ) {
            case CHEMISTRY:
                return "Вы участвуете в олимпиаде по <strong>Химии</strong>. Первый этап пройдет 22 ноября.";
            case BIOLOGY:
                return "Вы участвуете в олимпиаде по <strong>Биологии</strong>. Первый этап пройдет 25 ноября.";
        }
        return "";
    }

    public Block getInfoBlock() {
        Date now = new Date();
        if ( info.getStartDate() == null || info.getEndDate() == null ) {
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
