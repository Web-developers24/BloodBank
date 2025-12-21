package com.bbms.dao;

import com.bbms.model.Donor;
import java.util.List;

public class DonorDao extends AbstractDao<Donor, Long> {

    public List<Donor> findByBloodGroup(String bloodGroup) {
        return executeQuery(
                "FROM Donor WHERE bloodGroup = :bloodGroup",
                "bloodGroup", bloodGroup
        );
    }

    public List<Donor> findEligibleDonors() {
        return executeQuery("FROM Donor WHERE isEligible = true");
    }

    public List<Donor> findEligibleByBloodGroup(String bloodGroup) {
        return executeQuery(
                "FROM Donor WHERE bloodGroup = :bloodGroup AND isEligible = true",
                "bloodGroup", bloodGroup
        );
    }

    public List<Donor> findByPhone(String phone) {
        return executeQuery(
                "FROM Donor WHERE phone = :phone",
                "phone", phone
        );
    }

    public List<Donor> search(String keyword) {
        return executeQuery(
                "FROM Donor WHERE LOWER(fullName) LIKE :keyword OR phone LIKE :keyword",
                "keyword", "%" + keyword.toLowerCase() + "%"
        );
    }

    public List<Donor> findReadyToDonate() {
        return executeQuery(
                "FROM Donor d WHERE d.isEligible = true AND " +
                "(d.lastDonationDate IS NULL OR d.lastDonationDate < CURRENT_DATE - 90)"
        );
    }
}
