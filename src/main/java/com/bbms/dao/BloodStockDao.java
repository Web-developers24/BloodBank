package com.bbms.dao;

import com.bbms.config.HibernateUtil;
import com.bbms.model.BloodStock;
import org.hibernate.Session;

import java.util.List;
import java.util.Optional;

public class BloodStockDao extends AbstractDao<BloodStock, Long> {

    public List<BloodStock> findByBloodGroup(String bloodGroup) {
        return executeQuery(
                "FROM BloodStock WHERE bloodGroup = :bloodGroup AND status = 'AVAILABLE'",
                "bloodGroup", bloodGroup
        );
    }

    public List<BloodStock> findAvailable() {
        return executeQuery("FROM BloodStock WHERE status = 'AVAILABLE' AND unitsAvailable > 0");
    }

    public List<BloodStock> findByBloodGroupAndComponent(String bloodGroup, BloodStock.ComponentType componentType) {
        return executeQuery(
                "FROM BloodStock WHERE bloodGroup = :bloodGroup AND componentType = :componentType AND status = 'AVAILABLE'",
                "bloodGroup", bloodGroup,
                "componentType", componentType
        );
    }

    public List<BloodStock> findExpiringSoon(int days) {
        return executeQuery(
                "FROM BloodStock WHERE expiryDate <= CURRENT_DATE + :days AND expiryDate > CURRENT_DATE AND status = 'AVAILABLE'",
                "days", days
        );
    }

    public List<BloodStock> findExpired() {
        return executeQuery(
                "FROM BloodStock WHERE expiryDate < CURRENT_DATE AND status = 'AVAILABLE'"
        );
    }

    public List<BloodStock> findLowStock(int threshold) {
        return executeQuery(
                "FROM BloodStock WHERE unitsAvailable < :threshold AND status = 'AVAILABLE'",
                "threshold", threshold
        );
    }

    public int getTotalUnitsByBloodGroup(String bloodGroup) {
        try (Session session = getSession()) {
            Long result = session.createQuery(
                    "SELECT COALESCE(SUM(unitsAvailable), 0) FROM BloodStock WHERE bloodGroup = :bloodGroup AND status = 'AVAILABLE'",
                    Long.class)
                    .setParameter("bloodGroup", bloodGroup)
                    .getSingleResult();
            return result != null ? result.intValue() : 0;
        }
    }

    public List<Object[]> getStockSummaryByBloodGroup() {
        try (Session session = getSession()) {
            return session.createQuery(
                    "SELECT bloodGroup, SUM(unitsAvailable) FROM BloodStock WHERE status = 'AVAILABLE' GROUP BY bloodGroup ORDER BY bloodGroup",
                    Object[].class)
                    .getResultList();
        }
    }
}
