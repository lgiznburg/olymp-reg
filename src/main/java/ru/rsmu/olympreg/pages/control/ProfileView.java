package ru.rsmu.olympreg.pages.control;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.PageActivationContext;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import ru.rsmu.olympreg.dao.CompetitorDao;
import ru.rsmu.olympreg.dao.EmailDao;
import ru.rsmu.olympreg.entities.*;
import ru.rsmu.olympreg.services.EmailType;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author leonid.
 */
@RequiresRoles( value = {"admin", "manager", "moderator"}, logical = Logical.OR)
public class ProfileView {
    @Property
    @PageActivationContext
    private CompetitorProfile profile;

    @Property
    private AttachedFile attachedFile;

    @Property
    private ParticipationInfo participationInfo;

    @Property
    private String rejectReason;

    @Inject
    private CompetitorDao competitorDao;

    @Inject
    private EmailDao emailDao;

    @InjectPage
    private PreviewFile previewFile;

    public String getProfileSex() {
        if ( "F".equalsIgnoreCase( profile.getSex() ) ) {
            return "жен.";
        }
        if ( "М".equalsIgnoreCase( profile.getSex() ) ) {
            return "муж.";
        }
        return "";
    }

    public String getProfileSchoolLocation() {
        if ( profile.getSchoolLocation() != null ) {
            switch ( profile.getSchoolLocation() ) {
                case LOCATION_CITY:
                    return "в городе";
                case LOCATION_COUNTRY:
                    return "в сельской местности";
            }
        }
        return "";
    }

    public String getAttachedFileRole() {
        switch ( attachedFile.getAttachmentRole() ) {
            case PASSPORT:
                return "Паспорт";
            case SCHOOL:
                return "Справка из школы";
            case AGREEMENT:
                return "Согласие на обработку персональных данных";
            default:
                return "";
        }
    }

    public String getProfileRegion() {
        return profile.getRegion() != null ? profile.getRegion().getName() : "-";
    }

    public String getProfileForeignCountryName() {
        return profile.getCountry() != null ? profile.getCountry().getName() : "";
    }

    public String getOlympiadSubjectName() {
        String subjectName = "";
        switch ( participationInfo.getOlympiadSubject() ) {
            case CHEMISTRY:
                subjectName =  "Химия";
                break;
            case BIOLOGY:
                subjectName =  "Биология";
                break;
        }
        return String.format( "%s (%s этап)", subjectName, participationInfo.getStage() == 0 ? "1-ый" : "2-ой" );
    }

    public String getOlympiadResult() {
        if ( participationInfo.getResult() == null ) {
            return "Еще не определены";
        }
        else if( participationInfo.getResult() < 0 ) {
            return "Неявка";
        }
        else {
            return String.valueOf( participationInfo.getResult() ) + " баллов";
        }
    }

    public boolean onApproveParticipation( ParticipationInfo info ) {
        info.setApproved( true );
        competitorDao.save( info );

        Map<String,Object> model = new HashMap<>();
        User competitor = info.getProfile().getUser();

        model.put( "fullName", competitor.getFullName() );
        String subjectName = "";
        switch ( info.getOlympiadSubject() ) {
            case CHEMISTRY:
                subjectName =  "Химии";
                break;
            case BIOLOGY:
                subjectName =  "Биологии";
                break;
        }
        model.put("subjectName", subjectName);

        EmailQueue emailQueue = new EmailQueue();
        emailQueue.setEmailType( EmailType.SECOND_STAGE_APPROVED );
        emailQueue.setEmailAddress( competitor.getUsername() );
        emailQueue.setUser( competitor );
        ObjectMapper mapper = new ObjectMapper();
        try {
            emailQueue.setModel( mapper.writeValueAsString( model ) );
        } catch (IOException e) {
            // do nothing
        }
        emailDao.save( emailQueue );
        return true;
    }

    public boolean isApproveAvailable() {
        return profile.getParticipation().stream()
                .anyMatch( pr -> !pr.getApproved() );
    }

    public void onSuccess() {
        Map<String,Object> model = new HashMap<>();
        User competitor = profile.getUser();

        model.put( "fullName", competitor.getFullName() );
        model.put("reason", rejectReason.replace( "\n", "<br/>" ));

        EmailQueue emailQueue = new EmailQueue();
        emailQueue.setEmailType( EmailType.SECOND_STAGE_REJECTED );
        emailQueue.setEmailAddress( competitor.getUsername() );
        emailQueue.setUser( competitor );
        ObjectMapper mapper = new ObjectMapper();
        try {
            emailQueue.setModel( mapper.writeValueAsString( model ) );
        } catch (IOException e) {
            // do nothing
        }
        emailDao.save( emailQueue );
    }

    public String getAttachedURI() {
        return previewFile.getUploadedLink( attachedFile.getId() );
    }
}
