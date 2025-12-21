package com.bbms.dao;

import com.bbms.model.Donation;
import java.time.LocalDate;
import java.util.List;

public class DonationDao extends AbstractDao<Donation, Long> {

    public List<Donation> findByDonorId(Long donorId) {
        return executeQuery(
                "FROM Donation WHERE donor.id = :donorId ORDER BY donationDate DESC",
                "donorId", donorId
        );
    }

    public List<Donation> findByDateRange(LocalDate startDate, LocalDate endDate) {
        return executeQuery(
                "FROM Donation WHERE donationDate BETWEEN :startDate AND :endDate ORDER BY donationDate DESC",
                "startDate", startDate,
                "endDate", endDate
        );
    }

    public List<Donation> findByBloodGroup(String bloodGroup) {
        return executeQuery(
                "FROM Donation WHERE bloodGroup = :bloodGroup ORDER BY donationDate DESC",
                "bloodGroup", bloodGroup
        );
    }

    public List<Donation> findRecentDonations(int limit) {
        return executeQuery(
                "FROM Donation ORDER BY donationDate DESC"
        ).stream().limit(limit).toList();
    }

    public List<Donation> findByStatus(Donation.DonationStatus status) {
        return executeQuery(
                "FROM Donation WHERE status = :status ORDER BY donationDate DESC",
                "status", status
        );
    }
}
