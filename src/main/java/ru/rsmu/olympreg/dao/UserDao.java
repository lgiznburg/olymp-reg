package ru.rsmu.olympreg.dao;


import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import ru.rsmu.olympreg.entities.*;

import java.util.Date;
import java.util.List;

/**
 * @author leonid.
 */
public interface UserDao extends BaseDao {
    User findByUsername( String username );

    String encrypt( String password );

    List<User> finUserForRole( UserRoleName roleName );

    @CommitAfter
    int getNextPersonalNumber();

    UserRole findRole( UserRoleName roleName );

    CompetitorProfile findProfile( User user );

    List<UserCandidate> findOutdatedCandidates( Date time );
}
