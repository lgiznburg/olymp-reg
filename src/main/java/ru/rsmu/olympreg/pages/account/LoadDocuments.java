package ru.rsmu.olympreg.pages.account;

import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.io.IOUtils;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.ValueEncoderSource;
import org.apache.tapestry5.upload.components.Upload;
import org.apache.tapestry5.upload.services.UploadedFile;
import ru.rsmu.olympreg.dao.SystemPropertyDao;
import ru.rsmu.olympreg.dao.UserDao;
import ru.rsmu.olympreg.entities.*;
import ru.rsmu.olympreg.entities.system.StoredPropertyName;
import ru.rsmu.olympreg.pages.Index;
import ru.rsmu.olympreg.services.SecurityUserHelper;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author leonid.
 */
@RequiresRoles( "competitor" )
@Import( module = "bootstrap/collapse" )
public class LoadDocuments {

    @Property
    private User user;

    @Property
    private CompetitorProfile profile;

    @Property
    private AttachedFile attachedTemp;

    @Property
    private UploadedFile agreementFile;

    @Property
    private UploadedFile schoolFile;

    @Property
    private UploadedFile passportFile;

    @Property
    @Persist(PersistenceConstants.FLASH)
    private String formErrorMessage;

    @Inject
    private UserDao userDao;

    @Inject
    private SystemPropertyDao systemPropertyDao;

    @Inject
    private SecurityUserHelper securityUserHelper;

    @Inject
    private ValueEncoderSource valueEncoderSource;

    @InjectComponent
    private Form documentsForm;

    @InjectComponent
    private Upload passportFileField, agreementFileField, schoolFileField;

    @Inject
    private Messages messages;

    public Object onActivate() {
        prepare();
        if ( profile == null ) return Index.class;
        return null;
    }

/*
    public void onPrepareForRender() {
        prepare();
    }
*/

/*
    public void onPrepareForSubmit() {
        prepare();
    }
*/

    private void prepare() {
        user = securityUserHelper.getCurrentUser();
        profile = userDao.findProfile( user );
    }

    public List<AttachedFile> getPassportFiles() {
        return getSpecificAttachments( AttachmentRole.PASSPORT );
    }

    public List<AttachedFile> getSchoolFiles() {
        return getSpecificAttachments( AttachmentRole.SCHOOL );
    }

    public List<AttachedFile> getAgreementFiles() {
        return getSpecificAttachments( AttachmentRole.AGREEMENT );
    }

    private List<AttachedFile> getSpecificAttachments( AttachmentRole role ) {
        if ( profile.getAttachments() != null ) {
            return profile.getAttachments().stream().filter( at -> at.getAttachmentRole() == role )
                    .collect( Collectors.toList());
        }
        return Collections.emptyList();
    }

    public ValueEncoder<AttachedFile> getAttachedFileEncoder() {
        return valueEncoderSource.getValueEncoder( AttachedFile.class );
    }

    public void onDeleteFile( AttachedFile fileToDelete ) {
        AttachedFileContent content = userDao.find( AttachedFileContent.class, fileToDelete.getContentId() );
        //profile.getAttachments().remove( fileToDelete );
        userDao.delete( fileToDelete );
        if ( content != null ) {
            userDao.delete( content );
        }
    }

    public void onValidateFromDocumentsForm() {
        if ( passportFile != null && !passportFile.getContentType().matches( "(image.*)|(application/pdf)" ) ) {
            formErrorMessage = messages.get( "incorrect-file-type" );
            documentsForm.recordError( passportFileField, messages.get( "incorrect-file-type" ) );
        }
        if ( agreementFile != null && !agreementFile.getContentType().matches( "(image.*)|(application/pdf)" ) ) {
            formErrorMessage = messages.get( "incorrect-file-type" );
            documentsForm.recordError( agreementFileField, messages.get( "incorrect-file-type" ) );
        }
        if ( schoolFile != null && !schoolFile.getContentType().matches( "(image.*)|(application/pdf)" ) ) {
            formErrorMessage = messages.get( "incorrect-file-type" );
            documentsForm.recordError( schoolFileField, messages.get( "incorrect-file-type" ) );
        }

    }

    public void onSuccessFromDocumentsForm() {
        try {
            if ( documentsForm.isValid() ) {
                if ( passportFile != null  ) {
                    saveFileContent( passportFile, AttachmentRole.PASSPORT );
                }
                if ( agreementFile != null  ) {
                    saveFileContent( agreementFile, AttachmentRole.AGREEMENT );
                }
                if ( schoolFile != null ) {
                    saveFileContent( schoolFile, AttachmentRole.SCHOOL );
                }
            }
        } catch (IOException e) {
            //e.printStackTrace();
        }
    }

    private void saveFileContent( UploadedFile uploadedFile, AttachmentRole role ) throws IOException {
        AttachedFileContent content = new AttachedFileContent();
        content.setContent( IOUtils.toByteArray( uploadedFile.getStream() ) );
        userDao.save( content );

        AttachedFile attachedFile = new AttachedFile();
        attachedFile.setContentId( content.getId() );
        attachedFile.setAttachmentRole( role );
        attachedFile.setContentType( uploadedFile.getContentType() );
        attachedFile.setSourceName( uploadedFile.getFileName() );
        attachedFile.setSize( content.getContent().length );
        profile.addAttachment( attachedFile );

        userDao.save( attachedFile );
        userDao.evict( content );
    }

    Object onUploadException( FileUploadException ex)
    {
        formErrorMessage = messages.get( "incorrect-file-size" );

        return this;
    }

    public String getAgreementLink(){
        return systemPropertyDao.getProperty( StoredPropertyName.AGREEMENT_FILE_LOCATION );
    }
}
