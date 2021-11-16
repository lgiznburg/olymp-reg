package ru.rsmu.olympreg.pages.control;

import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.PageActivationContext;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.internal.services.LinkSource;
import org.apache.tapestry5.ioc.annotations.Inject;
import ru.rsmu.olympreg.dao.UserDao;
import ru.rsmu.olympreg.entities.AttachedFile;
import ru.rsmu.olympreg.entities.AttachedFileContent;
import ru.rsmu.olympreg.viewentities.FileNameTransliterator;
import ru.rsmu.olympreg.viewentities.download.AttachmentFile;

/**
 * @author leonid.
 */
public class PreviewFile {
    @Property
    @PageActivationContext
    private AttachedFile file;

    @Inject
    private UserDao userDao;

    @Inject
    private LinkSource linkSource;

    public String getUploadedLink(Long id) {
        return linkSource.createPageRenderLink( PreviewFile.class.getSimpleName(), false, id ).toURI();
    }

    public StreamResponse onActivate() {
        AttachedFileContent attachedFileContent = userDao.find( AttachedFileContent.class, file.getContentId() );
        return new AttachmentFile( attachedFileContent.getContent(), FileNameTransliterator.transliterateRuEn(file.getSourceName()) ) {
            @Override
            public String getContentType() {
                return file.getContentType();
            }
        };
    }
}
