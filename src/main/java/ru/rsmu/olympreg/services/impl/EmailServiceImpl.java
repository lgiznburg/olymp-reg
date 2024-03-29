package ru.rsmu.olympreg.services.impl;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.tools.ToolContext;
import org.apache.velocity.tools.ToolManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.rsmu.olympreg.dao.EmailDao;
import ru.rsmu.olympreg.dao.SystemPropertyDao;
import ru.rsmu.olympreg.entities.AttachedFile;
import ru.rsmu.olympreg.entities.AttachedFileContent;
import ru.rsmu.olympreg.entities.User;
import ru.rsmu.olympreg.entities.system.StoredPropertyName;
import ru.rsmu.olympreg.services.EmailService;
import ru.rsmu.olympreg.services.EmailType;
import ru.rsmu.olympreg.viewentities.FileNameTransliterator;

import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Map;
import java.util.Properties;

/**
 * @author leonid.
 */
public class EmailServiceImpl implements EmailService {

    private static final Logger log = LoggerFactory.getLogger( EmailService.class );

    private final VelocityEngine velocityEngine;

    @Inject
    private SystemPropertyDao systemPropertyDao;

    @Inject
    private EmailDao emailDao;

    public EmailServiceImpl() {
        velocityEngine = new VelocityEngine();
        try {
            Properties velocityProp = new Properties();
            InputStream velocityFile = getClass().getResourceAsStream( "/velocity.properties" );
            velocityProp.load( velocityFile );
            velocityEngine.init( velocityProp );
        } catch (Exception e) {
            log.error( "Can't open velocity.properties file", e );
        }

    }

    public void sendEmail( User user, EmailType emailType, Map<String,Object> model ) {
        try {
            HtmlEmail email = createHtmlEmail( emailType, model );
            email.addTo( user.getUsername(), user.getFullName() );
            email.send();

        } catch (EmailException e) {
            log.error( String.format( "Email to %s  wasn't sent", user.getUsername() ), e );
        }
    }

    public void sendEmail( String to, EmailType emailType, Map<String,Object> model ) {
        try {

            HtmlEmail email = createHtmlEmail( emailType, model );
            email.addTo( to );
            email.send();

        } catch (EmailException e) {
            log.error( String.format( "Email to %s wasn't sent", to ), e );
        }
    }


    private HtmlEmail createHtmlEmail( EmailType emailType, Map<String,Object> model) throws EmailException {
        final HtmlEmail htmlEmail = new HtmlEmail();
        htmlEmail.setHostName( systemPropertyDao.getProperty( StoredPropertyName.SENDMAIL_HOST ) );
        String hostLogin = systemPropertyDao.getProperty( StoredPropertyName.SENDMAIL_LOGIN);
        String hostPassword = systemPropertyDao.getProperty( StoredPropertyName.SENDMAIL_PASSWORD);
        if ( StringUtils.isNotBlank( hostLogin ) && StringUtils.isNotBlank(hostPassword)) {
            htmlEmail.setAuthentication(hostLogin, hostPassword);
        }

        if (systemPropertyDao.getPropertyAsInt( StoredPropertyName.SENDMAIL_PORT ) > 0 )
            htmlEmail.setSmtpPort( systemPropertyDao.getPropertyAsInt( StoredPropertyName.SENDMAIL_PORT ) );

        htmlEmail.setStartTLSEnabled( systemPropertyDao.getPropertyAsInt( StoredPropertyName.SENDMAIL_USE_TLS ) > 0 );
        htmlEmail.setSSLOnConnect( systemPropertyDao.getPropertyAsInt( StoredPropertyName.SENDMAIL_USE_SSL ) > 0 );
        htmlEmail.setSslSmtpPort( systemPropertyDao.getProperty( StoredPropertyName.SENDMAIL_SSL_PORT) );

        htmlEmail.setFrom( systemPropertyDao.getProperty( StoredPropertyName.EMAIL_FROM_ADDRESS ),
                systemPropertyDao.getProperty( StoredPropertyName.EMAIL_FROM_SIGNATURE ),
                "UTF-8" );
        htmlEmail.setBounceAddress( systemPropertyDao.getProperty( StoredPropertyName.EMAIL_FROM_ADDRESS ) );
            /*try {
                List<InternetAddress> replyToAddresses = new ArrayList<>();
                replyToAddresses.add( new InternetAddress("noreply@rsmu.ru") );
                htmlEmail.setReplyTo( replyToAddresses );
            } catch (AddressException e) {
                // what?
            }*/

        model.put( "replyEmail", systemPropertyDao.getProperty( StoredPropertyName.EMAIL_FROM_ADDRESS ) );
        model.put( "signature", systemPropertyDao.getProperty( StoredPropertyName.EMAIL_FROM_SIGNATURE ) );
        htmlEmail.setSubject( evaluateSubject( emailType.getSubject(), model ) );
        htmlEmail.setHtmlMsg( generateEmailMessage( emailType.getFileName(), model ) );

        if ( emailType == EmailType.FREE_INFO ) {
            AttachedFile attachedFile = emailDao.findEmailAttachment();
            if ( attachedFile != null ) {
                AttachedFileContent content = emailDao.find( AttachedFileContent.class, attachedFile.getContentId() );
                if ( content != null ) {
                    htmlEmail.embed(  new ByteArrayDataSource( content.getContent(), attachedFile.getContentType() ),
                            FileNameTransliterator.transliterateRuEn( attachedFile.getSourceName() ) );
                }
            }
        }

        return htmlEmail;
    }

    private String generateEmailMessage(final String template, final Map<String,Object> model) throws EmailException {

        try {
            final StringWriter message = new StringWriter();
            final ToolManager toolManager = new ToolManager();
            final ToolContext toolContext = toolManager.createContext();
            final VelocityContext context = new VelocityContext(model, toolContext);

            velocityEngine.mergeTemplate( template, "UTF-8", context, message );
            return message.getBuffer().toString();

        } catch (Exception e) {
            throw new EmailException("Can't create email body", e);
        }
    }

    private String evaluateSubject( final String subjectTemplate, final Map<String,Object> model) throws EmailException {
        try {
            final StringWriter subject = new StringWriter();
            final ToolManager toolManager = new ToolManager();
            final ToolContext toolContext = toolManager.createContext();
            final VelocityContext context = new VelocityContext(model, toolContext);

            velocityEngine.evaluate( context, subject, "", subjectTemplate );
            return subject.getBuffer().toString();

        } catch (Exception e) {
            throw new EmailException("Can't create email subject", e);
        }

    }

}
