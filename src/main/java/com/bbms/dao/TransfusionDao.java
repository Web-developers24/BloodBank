package com.bbms.dao;

import com.bbms.model.Transfusion;
import java.time.LocalDate;
import java.util.List;

public class TransfusionDao extends AbstractDao<Transfusion, Long> {

    public List<Transfusion> findByRecipientId(Long recipientId) {
        return executeQuery(
                "FROM Transfusion WHERE recipient.id = :recipientId ORDER BY transfusionDate DESC",
                "recipientId", recipientId
        );
    }

    public List<Transfusion> findByDateRange(LocalDate startDate, LocalDate endDate) {
        return executeQuery(
                "FROM Transfusion WHERE transfusionDate BETWEEN :startDate AND :endDate ORDER BY transfusionDate DESC",
                "startDate", startDate,
                "endDate", endDate
        );
    }

    public List<Transfusion> findWithReactions() {
        return executeQuery(
                "FROM Transfusion WHERE reactionObserved = true ORDER BY transfusionDate DESC"
        );
    }

    public List<Transfusion> findRecentTransfusions(int limit) {
        return executeQuery(
                "FROM Transfusion ORDER BY transfusionDate DESC"
        ).stream().limit(limit).toList();
    }
}
