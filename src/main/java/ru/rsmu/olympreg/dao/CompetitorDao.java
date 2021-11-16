package ru.rsmu.olympreg.dao;

import ru.rsmu.olympreg.entities.CompetitorProfile;
import ru.rsmu.olympreg.viewentities.CompetitorFilter;
import ru.rsmu.olympreg.viewentities.SortCriterion;

import java.util.List;

/**
 * @author leonid.
 */
public interface CompetitorDao extends BaseDao {
    int countProfiles( CompetitorFilter filter );

    List<CompetitorProfile> findProfiles( CompetitorFilter filter, List<SortCriterion> toSortCriteria );

    int countSubjectSelectedProfiles();

    List<Object[]> findDetailedStats();

    int countFilledBaseInfo();

    int countUploadCompleted();
}
