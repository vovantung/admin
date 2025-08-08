package txu.admin.mainapp.dao;


import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import txu.admin.mainapp.base.AbstractDao;
import txu.admin.mainapp.entity.WeeklyReportEntity;

import java.util.Date;
import java.util.List;

@Repository
public class WeeklyReportDao extends AbstractDao<WeeklyReportEntity> {

    @Transactional
    public WeeklyReportEntity save(WeeklyReportEntity weeklyReportEntity) {
        if (weeklyReportEntity.getId() == null || weeklyReportEntity.getId() == 0) {
            persist(weeklyReportEntity);
            return weeklyReportEntity;
        } else {
            return merge(weeklyReportEntity);
        }
    }

    @Override
    public WeeklyReportEntity findById(Object Id) {
        return super.findById(Id);
    }

    @Transactional
    public void remove(WeeklyReportEntity weeklyReportEntity) {
        weeklyReportEntity = merge(weeklyReportEntity);
        getEntityManager().remove(weeklyReportEntity);
    }


    public List<WeeklyReportEntity> getWithLimit(int limit) {
        StringBuilder queryString = new StringBuilder("SELECT W FROM WeeklyReportEntity AS W ORDER BY W.uploadedAt DESC");
        Query query = getEntityManager().createQuery(queryString.toString());
        query.setMaxResults(limit);
        return getRessultList(query);

    }

    public List<WeeklyReportEntity> getFromDateToDate(Date from, Date to) {
        StringBuilder queryString = new StringBuilder("SELECT W FROM WeeklyReportEntity AS W WHERE W.uploadedAt >=:from AND W.uploadedAt <=: to  ORDER BY W.uploadedAt DESC");
        Query query = getEntityManager().createQuery(queryString.toString());
        query.setParameter("from", from);
        query.setParameter("to", to);
        return getRessultList(query);

    }

}
