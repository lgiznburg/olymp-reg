package ru.rsmu.olympreg.pages.account;

import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.PageActivationContext;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.internal.services.LinkSource;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.tynamo.security.services.SecurityService;
import ru.rsmu.olympreg.dao.UserDao;
import ru.rsmu.olympreg.entities.*;
import ru.rsmu.olympreg.services.SecurityUserHelper;
import ru.rsmu.olympreg.viewentities.FileNameTransliterator;
import ru.rsmu.olympreg.viewentities.download.AttachmentFile;

import java.util.Objects;

/**
 * @author leonid.
 */
public class PreviewDiploma {
    @Property
    @PageActivationContext
    private AttachedFile file;

    @Inject
    private UserDao userDao;

    @Inject
    private LinkSource linkSource;

    @Inject
    private SecurityUserHelper securityUserHelper;

    @Inject
    private SecurityService securityService;

    public String getUploadedLink(Long id) {
        return linkSource.createPageRenderLink( PreviewDiploma.class.getSimpleName(), false, id ).toURI();
    }

    public StreamResponse onActivate() {
        if ( securityService.isUser() && securityService.hasRole( UserRoleName.competitor.name() )) {
            User user = securityUserHelper.getCurrentUser();
            if ( !file.getProfile().getUser().equals( user ) ) {
                return null;
            }
        }
        AttachedFileContent attachedFileContent = userDao.find( AttachedFileContent.class, file.getContentId() );
        return new AttachmentFile( attachedFileContent.getContent(), FileNameTransliterator.transliterateRuEn(file.getSourceName()) ) {
            @Override
            public String getContentType() {
                return file.getContentType();
            }
        };
    }
}
