package ru.rsmu.olympreg.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.rsmu.olympreg.dao.CompetitorDao;
import ru.rsmu.olympreg.dao.EmailDao;
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
public class SendUncompletedNotificationTask implements Runnable {
    private final EmailDao emailDao;

    private final CompetitorDao competitorDao;

    public SendUncompletedNotificationTask( EmailDao emailDao, CompetitorDao competitorDao ) {
        this.emailDao = emailDao;
        this.competitorDao = competitorDao;
    }

    @Override
    public void run() {
        ObjectMapper mapper = new ObjectMapper();
        List<CompetitorProfile> profiles = competitorDao.findUncompletedProfiles();
        for ( CompetitorProfile profile : profiles ) {
            Map<String,Object> model = new HashMap<>();

            model.put( "fullName", profile.getUser().getFullName() );

            EmailQueue emailQueue = new EmailQueue();
            emailQueue.setEmailType( EmailType.FIRST_STAGE_UNCOMPLETED );
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
