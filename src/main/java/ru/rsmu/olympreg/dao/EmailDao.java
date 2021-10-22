package ru.rsmu.olympreg.dao;

import ru.rsmu.olympreg.entities.EmailQueue;

import java.util.List;

/**
 * @author leonid.
 */
public interface EmailDao extends BaseDao {
    List<EmailQueue> findNewEmails();
}
