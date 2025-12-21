package com.bbms.dao;

import com.bbms.model.Recipient;
import java.util.List;

public class RecipientDao extends AbstractDao<Recipient, Long> {

    public List<Recipient> findByBloodGroup(String bloodGroup) {
        return executeQuery(
                "FROM Recipient WHERE bloodGroup = :bloodGroup",
                "bloodGroup", bloodGroup
        );
    }

    public List<Recipient> findByHospital(String hospitalName) {
        return executeQuery(
                "FROM Recipient WHERE LOWER(hospitalName) LIKE :hospitalName",
                "hospitalName", "%" + hospitalName.toLowerCase() + "%"
        );
    }

    public List<Recipient> search(String keyword) {
        return executeQuery(
                "FROM Recipient WHERE LOWER(fullName) LIKE :keyword OR phone LIKE :keyword",
                "keyword", "%" + keyword.toLowerCase() + "%"
        );
    }
}
