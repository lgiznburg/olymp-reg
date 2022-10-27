package ru.rsmu.olympreg.dao;

import ru.rsmu.olympreg.entities.OlympiadConfig;
import ru.rsmu.olympreg.entities.OlympiadSubject;

import java.util.List;

/**
 * @author leonid.
 */
public interface OlympiadDao extends BaseDao {
    List<OlympiadConfig> getAllConfigs();

    OlympiadConfig findActiveForClassAndSubject( int classNumber, OlympiadSubject subject );
    boolean checkRegistrationOpen();
}
