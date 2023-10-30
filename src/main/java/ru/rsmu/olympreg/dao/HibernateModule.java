package ru.rsmu.olympreg.dao;

import org.apache.commons.io.IOUtils;
import org.apache.tapestry5.hibernate.HibernateTransactionAdvisor;
import org.apache.tapestry5.ioc.MethodAdviceReceiver;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.Match;
import ru.rsmu.olympreg.dao.internal.*;
import ru.rsmu.olympreg.entities.*;
import ru.rsmu.olympreg.seedentity.hibernate.SeedEntity;
import ru.rsmu.olympreg.seedentity.hibernate.SeedEntityImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.LinkedList;

/**
 * @author leonid.
 */
public class HibernateModule {
    public static void bind( ServiceBinder binder)
    {
        binder.bind( UserDao.class, UserDaoImpl.class );
        binder.bind( SystemPropertyDao.class, SystemPropertyDaoImpl.class );
        binder.bind( EmailDao.class, EmailDaoImpl.class );
        binder.bind( CompetitorDao.class, CompetitorDaoImpl.class );
        binder.bind( OlympiadDao.class, OlympiadDaoImpl.class );
        binder.bind( CountryDao.class, CountryDaoImpl.class );

        //seed entity - initial DB population
        binder.bind(SeedEntity.class, SeedEntityImpl.class);
    }

    @Match("*Dao")
    public static void adviseTransactions( HibernateTransactionAdvisor advisor,
                                           MethodAdviceReceiver receiver)
    {
        advisor.addTransactionCommitAdvice(receiver);
    }

    //populate with initial values
    @Contribute(SeedEntity.class)
    public static void addSeedEntities( OrderedConfiguration<Object> configuration) {
        populateUsers( configuration );
        populateCountries( configuration );
    }

    private static void populateUsers( OrderedConfiguration<Object> configuration ) {
        //Roles:
        UserRole adminRole = new UserRole();
        adminRole.setRoleName( UserRoleName.admin );
        configuration.add( "adminRole", adminRole );

        UserRole managerRole = new UserRole();
        managerRole.setRoleName( UserRoleName.manager );
        configuration.add( "managerRole", managerRole );

        UserRole moderatorRole = new UserRole();
        moderatorRole.setRoleName( UserRoleName.manager );
        configuration.add( "moderatorRole", moderatorRole );

        UserRole competitorRole = new UserRole();
        competitorRole.setRoleName( UserRoleName.competitor );
        configuration.add( "competitorRole", competitorRole );

        User admin = new User();
        admin.setUsername( "prk_admin@rsmu.ru" );
        admin.setFirstName( "Эдельвейс" );
        admin.setMiddleName( "Захарович" );
        admin.setLastName( "Машкин" );
        admin.setPassword( "6A8AEAFFD00E99F5B377B084FA577E3B" );
        admin.setRoles( new LinkedList<>() );
        admin.getRoles().add( adminRole );
        configuration.add( "admin", admin );

        User me = new User();
        me.setUsername( "ginzburg_ld@rsmu.ru" );
        me.setFirstName( "Леонид" );
        me.setMiddleName( "Давидович" );
        me.setLastName( "Гинзбург" );
        me.setPassword( "33A1628D8BF774777141A6AF3B283DC4" );
        me.setRoles( new LinkedList<>() );
        me.getRoles().add( adminRole );
        configuration.add( "me", me );

        User pps = new User();
        pps.setUsername( "polyakov_ps@rsmu.ru" );
        pps.setFirstName( "Павел" );
        pps.setMiddleName( "Сергеевич" );
        pps.setLastName( "Поляков" );
        pps.setPassword( "25D3B13C5501D00F0E63E77CD8F6B4BB" );
        pps.setRoles( new LinkedList<>() );
        pps.getRoles().add( adminRole );
        configuration.add( "pps", pps );

        CompetitorCounter counter = new CompetitorCounter();
        Calendar calendar = Calendar.getInstance();
        counter.setYear( calendar.get( Calendar.YEAR ) );
        counter.setCounter( 1 );
        configuration.add( "counter", counter );
    }

    private static void populateCountries( OrderedConfiguration<Object> configuration ) {
        InputStream is = HibernateModule.class
                .getClassLoader().getResourceAsStream( "catalogs/countries.csv" );
        if ( is != null ) {
            BufferedReader br = new BufferedReader(new InputStreamReader( is, StandardCharsets.UTF_8));
            boolean skipFirstLine = true;
            try {
                for ( String strLine; (strLine = br.readLine()) != null; ) {
                    if ( skipFirstLine ) {
                        skipFirstLine = false;
                        continue;
                    }
                    String[] parts = strLine.split( ";" );
                    if ( parts.length > 5 ) {
                        Country country = new Country( parts[0], parts[1], parts[2], parts[3], parts[4] );
                        configuration.add( parts[3], country );
                    }
                }
            } catch (IOException e) {
                // Unable to read countries
            }
        }
    }
}
