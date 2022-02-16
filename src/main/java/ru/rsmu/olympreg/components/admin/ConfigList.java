package ru.rsmu.olympreg.components.admin;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import ru.rsmu.olympreg.dao.OlympiadDao;
import ru.rsmu.olympreg.entities.OlympiadConfig;
import ru.rsmu.olympreg.utils.CrudMode;

import java.util.List;

/**
 * @author leonid.
 */
public class ConfigList {
    // Parameters

    @Parameter(required = true)
    @Property
    private OlympiadConfig olympiadConfig;

    @Property
    private OlympiadConfig olympiadIt;

    @Inject
    private OlympiadDao olympiadDao;

    public List<OlympiadConfig> getOlympiadConfigList() {
        return olympiadDao.getAllConfigs();
    }

    public String getLinkCssClass() {
        if ( olympiadConfig != null && olympiadIt != null && olympiadConfig.getId() == olympiadIt.getId() ) {
            return "active";
        }
        return "";
    }

}
