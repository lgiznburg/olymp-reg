package ru.rsmu.olympreg.pages.control;

import org.apache.tapestry5.annotations.ActivationRequestParameter;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.PageActivationContext;
import org.apache.tapestry5.annotations.Property;
import ru.rsmu.olympreg.entities.AttachedFile;
import ru.rsmu.olympreg.entities.CompetitorProfile;
import ru.rsmu.olympreg.entities.ParticipationInfo;

/**
 * @author leonid.
 */
public class ProfileView {
    @Property
    @PageActivationContext
    private CompetitorProfile profile;

    @Property
    private AttachedFile attachedFile;

    @Property
    private ParticipationInfo participationInfo;

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
                return "Согласия на обработку персональных данных";
            default:
                return "";
        }
    }

    public String getOlympiadSubjectName() {
        switch ( participationInfo.getOlympiadSubject() ) {
            case CHEMISTRY:
                return "Химия";
            case BIOLOGY:
                return "Биология";
            default:
                return "";
        }
    }

    public String getAttachedURI() {
        return previewFile.getUploadedLink( attachedFile.getId() );
    }
}
