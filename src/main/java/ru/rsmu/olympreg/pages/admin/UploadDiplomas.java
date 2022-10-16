package ru.rsmu.olympreg.pages.admin;

import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.io.IOUtils;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.upload.services.UploadedFile;
import ru.rsmu.olympreg.dao.CompetitorDao;
import ru.rsmu.olympreg.entities.AttachedFile;
import ru.rsmu.olympreg.entities.AttachedFileContent;
import ru.rsmu.olympreg.entities.AttachmentRole;
import ru.rsmu.olympreg.entities.CompetitorProfile;

import javax.inject.Inject;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author leonid.
 */
public class UploadDiplomas {
    @Property
    private UploadedFile diplomaZipFile;


    @Persist(PersistenceConstants.FLASH)
    @Property
    private String message;

    @Inject
    private CompetitorDao competitorDao;

    public void onPrepareForRender() {
    }

    public void onPrepareForSubmit() {
        message = "";
    }

    Object onUploadException( FileUploadException ex)
    {
        message = "Upload exception: " + ex.getMessage();

        return this;
    }

    public boolean onSuccess() {
        if ( diplomaZipFile != null && diplomaZipFile.getFileName().matches( ".*\\.zip$" ) ) {
            ZipInputStream zipInputStream = new ZipInputStream( diplomaZipFile.getStream() );
            Pattern fileNamePattern = Pattern.compile( "\\d{9}" );
            int uploadCunt = 0;
            try {
                ZipEntry zipEntry = zipInputStream.getNextEntry();
                while ( zipEntry != null ) {
                    String diplomaName = zipEntry.getName();
                    Matcher nameMatcher = fileNamePattern.matcher( diplomaName );
                    if ( nameMatcher.find() && diplomaName.matches( "^[\\d\\w_-]*\\.pdf$" ) ) {
                        String personalNumber = nameMatcher.group();

                        CompetitorProfile profile = competitorDao.findProfile( personalNumber );
                        if ( profile != null ) {
                            AttachedFileContent content = new AttachedFileContent();
                            content.setContent( IOUtils.toByteArray( zipInputStream ) );
                            competitorDao.save( content );

                            AttachedFile attachedFile = new AttachedFile();
                            attachedFile.setContentId( content.getId() );
                            attachedFile.setAttachmentRole( AttachmentRole.DIPLOMA );
                            attachedFile.setContentType( "application/pdf" );
                            attachedFile.setSourceName( diplomaName );
                            attachedFile.setSize( content.getContent().length );
                            profile.addAttachment( attachedFile );

                            competitorDao.save( attachedFile );
                            competitorDao.evict( content );

                            uploadCunt++;
                        }
                    }
                    zipEntry = zipInputStream.getNextEntry();
                }
            } catch (IOException e) {
                message += "<br/>Upload exception: " + e.getMessage();
            }
            if ( message.length() > 0 ) {
                message += "<br/>";
            }
            message += "Загружено дипломов: " + uploadCunt;
        }
        return true;
    }

}
