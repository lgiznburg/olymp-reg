package ru.rsmu.olympreg.pages.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.io.IOUtils;
import org.apache.tapestry5.OptionGroupModel;
import org.apache.tapestry5.OptionModel;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.internal.OptionModelImpl;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.upload.services.UploadedFile;
import org.apache.tapestry5.util.AbstractSelectModel;
import ru.rsmu.olympreg.dao.CompetitorDao;
import ru.rsmu.olympreg.dao.EmailDao;
import ru.rsmu.olympreg.entities.*;
import ru.rsmu.olympreg.services.EmailType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author leonid.
 */
public class SendFreeEmail {

    @Property
    private OlympiadSubject olympiadSubject;

    @Property
    private UploadedFile emailAttachment;

    @Property
    private String emailText;

    @Persist(PersistenceConstants.FLASH)
    @Property
    private String message;

    @Property
    private Integer stage = 1;

    @Property
    private OlympiadSubject subject;

    @Inject
    private EmailDao emailDao;

    @Inject
    private CompetitorDao competitorDao;

    public void onPrepareForRender() {
    }

    public void onPrepareForSubmit() {
        message = "";
    }

    Object onUploadException( FileUploadException ex ) {
        message = "Upload exception: " + ex.getMessage();
        return this;
    }

    public SelectModel getStageSelectModel() {
        return new AbstractSelectModel() {
            @Override
            public List<OptionGroupModel> getOptionGroups() {
                return null;
            }

            @Override
            public List<OptionModel> getOptions() {
                List<OptionModel> options = new ArrayList<OptionModel>();
                options.add(new OptionModelImpl( "Первый этап", 0));
                options.add(new OptionModelImpl( "Второй этап", 1));
                return options;
            }
        };
    }

    public boolean onSuccess() {
        emailDao.removeOldEmailAttachment();  // remove old attachment todo: protect it before all emails have been sent
        try {  // save new email attachment
            if ( emailAttachment != null ) {
                AttachedFileContent content = new AttachedFileContent();
                content.setContent( IOUtils.toByteArray( emailAttachment.getStream() ) );
                emailDao.save( content );

                AttachedFile attachedFile = new AttachedFile();
                attachedFile.setContentId( content.getId() );
                attachedFile.setContentType( emailAttachment.getContentType() );
                attachedFile.setSourceName( emailAttachment.getFileName() );
                attachedFile.setSize( content.getContent().length );
                emailDao.save( attachedFile );
            }
        } catch (IOException e) {
            //
        }
        //create emails list
        List<CompetitorProfile> competitors = competitorDao.findStageParticipants( stage, subject );
        for ( CompetitorProfile profile : competitors ) {

            Map<String,Object> model = new HashMap<>();
            User competitor = profile.getUser();

            model.put( "fullName", competitor.getFullName() );
            String subjectName = "";
            switch ( subject ) {
                case CHEMISTRY:
                    subjectName =  "Химии";
                    break;
                case BIOLOGY:
                    subjectName =  "Биологии";
                    break;
            }
            model.put("subjectName", subjectName);
            model.put( "message", emailText.replace( "\\n", "<br/>" ) );

            EmailQueue emailQueue = new EmailQueue();
            emailQueue.setEmailType( EmailType.FREE_INFO );
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
        message = "Подготовлено писем для отправки: " + competitors.size();
        return true;
    }
}
