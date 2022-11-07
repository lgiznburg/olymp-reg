package ru.rsmu.olympreg.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.rsmu.olympreg.dao.CompetitorDao;
import ru.rsmu.olympreg.dao.EmailDao;
import ru.rsmu.olympreg.dao.SystemPropertyDao;
import ru.rsmu.olympreg.entities.CompetitorProfile;
import ru.rsmu.olympreg.entities.EmailQueue;
import ru.rsmu.olympreg.entities.system.StoredPropertyName;
import ru.rsmu.olympreg.services.EmailType;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author leonid.
 */
public class SendPreviousYearReminderTask implements Runnable {

    private final EmailDao emailDao;

    private final CompetitorDao competitorDao;

    private final SystemPropertyDao systemPropertyDao;

    public SendPreviousYearReminderTask( EmailDao emailDao, CompetitorDao competitorDao, SystemPropertyDao systemPropertyDao ) {
        this.emailDao = emailDao;
        this.competitorDao = competitorDao;
        this.systemPropertyDao = systemPropertyDao;
    }

    @Override
    public void run() {
        ObjectMapper mapper = new ObjectMapper();
        List<CompetitorProfile> profiles = competitorDao.findPreviousYearProfiles();
        for ( CompetitorProfile profile : profiles ) {
            Map<String,Object> model = new HashMap<>();

            model.put( "fullName", profile.getUser().getFullName() );
            String thisServerUri = systemPropertyDao.getProperty( StoredPropertyName.MY_OWN_URI );
            model.put( "siteUrl", thisServerUri );

            EmailQueue emailQueue = new EmailQueue();
            emailQueue.setEmailType( EmailType.LAST_YEAR_REMIND );
            emailQueue.setEmailAddress( profile.getUser().getUsername() );
            emailQueue.setUser( profile.getUser() );
            try {
                emailQueue.setModel( mapper.writeValueAsString( model ) );
            } catch (IOException e) {
                // do nothing
            }
            emailDao.save( emailQueue );
        }

    }
}
