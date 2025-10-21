package org.example.dao;

import org.example.config.HibernateSessionHelper;
import org.example.model.BloodStock;
import java.util.List;

public class BloodStockDao {

    /**
     * ✅ Save or update a blood stock entry.
     * If blood group exists → update units & last updated time.
     * Otherwise → create a new entry.
     */
    public void saveOrUpdateStock(BloodStock stock) {
        HibernateSessionHelper.doInTransaction(session -> {
            BloodStock existing = session
                    .createQuery("from BloodStock where bloodGroup = :bg", BloodStock.class)
                    .setParameter("bg", stock.getBloodGroup())
                    .uniqueResult();

            if (existing == null) {
                session.persist(stock);
                System.out.println("🩸 New stock added for: " + stock.getBloodGroup());
            } else {
                existing.setUnitsAvailable(stock.getUnitsAvailable());
                existing.setLastUpdated(stock.getLastUpdated());
                session.merge(existing);
                System.out.println("♻️ Stock updated for: " + stock.getBloodGroup());
            }
            return null;
        });
    }

    /**
     * ✅ Fetch blood stock by group.
     */
    public BloodStock getStockByGroup(String bg) {
        return HibernateSessionHelper.doInTransaction(session ->
                session.createQuery("from BloodStock where bloodGroup = :bg", BloodStock.class)
                        .setParameter("bg", bg)
                        .uniqueResult()
        );
    }

    /**
     * ✅ Fetch all blood stock entries for the TableView (BloodStockController)
     */
    public List<BloodStock> getAllStocks() {
        return HibernateSessionHelper.doInTransaction(session ->
                session.createQuery("from BloodStock order by bloodGroup", BloodStock.class)
                        .list()
        );
    }

    /**
     * ✅ Optional utility: delete all stock (for testing/demo reset)
     */
    public void clearAllStocks() {
        HibernateSessionHelper.doInTransaction(session -> {
            session.createMutationQuery("delete from BloodStock").executeUpdate();
            System.out.println("🧹 All blood stock records cleared.");
            return null;
        });
    }
}
