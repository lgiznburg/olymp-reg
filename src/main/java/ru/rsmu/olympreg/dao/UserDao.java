package ru.rsmu.olympreg.dao;


import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import ru.rsmu.olympreg.entities.SubjectRegion;
import ru.rsmu.olympreg.entities.User;
import ru.rsmu.olympreg.entities.UserRole;
import ru.rsmu.olympreg.entities.UserRoleName;

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
}
