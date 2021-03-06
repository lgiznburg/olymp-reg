package ru.rsmu.olympreg.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import ru.rsmu.olympreg.dao.EmailDao;
import ru.rsmu.olympreg.entities.EmailQueue;
import ru.rsmu.olympreg.entities.EmailQueueStatus;
import ru.rsmu.olympreg.services.EmailService;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author leonid.
 */
public class SendEmailJob implements Job {

    @Inject
    private EmailDao emailDao;

    @Inject
    private EmailService emailService;

    @Override
    public void execute( JobExecutionContext context ) throws JobExecutionException {
        List<EmailQueue> portion = emailDao.findNewEmails();
        for ( EmailQueue emailQueue : portion ) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                Map<String, Object> parsed = mapper.readValue( emailQueue.getModel(), new TypeReference<Map<String, Object>>() {} );

                if ( emailQueue.getUser() != null ) {
                    emailService.sendEmail( emailQueue.getUser(), emailQueue.getEmailType(), parsed );
                }
                else {
                    emailService.sendEmail( emailQueue.getEmailAddress(), emailQueue.getEmailType(), parsed );
                }

                emailQueue.setStatus( EmailQueueStatus.SUCCESS );
            }
            catch (Exception e) {
                emailQueue.setStatus( EmailQueueStatus.ERROR );
            }
            emailQueue.setUpdatedDate( new Date() );
            emailDao.save( emailQueue );
        }
    }
}
