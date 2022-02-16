package ru.rsmu.olympreg.components.admin;

import org.apache.tapestry5.annotations.Events;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.hibernate.HibernateGridDataSource;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.BeanModelSource;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;
import org.hibernate.Session;
import ru.rsmu.olympreg.dao.UserDao;
import ru.rsmu.olympreg.entities.User;
import ru.rsmu.olympreg.viewentities.CompetitorFilter;
import ru.rsmu.olympreg.viewentities.UserDataSource;


/**
 * This component will trigger the following events on its container:
 * {@link UsersList#USER_SELECTED}(Long userId)
 *
 * @author leonid.
 */
@Events( {UsersList.USER_SELECTED} )  // just for documentation
public class UsersList {
    public static final String USER_SELECTED = "userSelected";

    @Parameter(required = true)
    @Property
    private User user;

    @Property
    private User userIt;

    @Property
    private CompetitorFilter filter = new CompetitorFilter();

    @Inject
    private UserDao userDao;

    @Inject
    private BeanModelSource beanModelSource;

    @Inject
    private Messages messages;

    @Inject
    private Request request;

    @InjectComponent
    private Zone userListZone;

    @Inject
    private AjaxResponseRenderer ajaxResponseRenderer;

    public GridDataSource getUsersDataSource() {
        return new UserDataSource( userDao, filter );
    }

    public String getLinkCssClass() {
        if ( user != null && userIt != null && user.getId() == userIt.getId() ) {
            return "active";
        }
        return "";
    }

    public void onSuccessFromFilterForm() {
        if ( request.isXHR() ) {
            ajaxResponseRenderer.addRender( userListZone );
        }
    }
}
