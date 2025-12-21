package com.bbms.service;

import com.bbms.dao.DonorDao;
import com.bbms.dao.DonationDao;
import com.bbms.model.Donor;
import com.bbms.model.Donation;
import com.bbms.model.BloodStock;
import com.bbms.util.BloodCompatibility;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class DonorService {

    private static final Logger logger = LogManager.getLogger(DonorService.class);
    private static final int DONATION_COOLDOWN_DAYS = 90;
    private static final double MIN_WEIGHT_KG = 50.0;
    private static final int MIN_AGE = 18;
    private static final int MAX_AGE = 65;

    private final DonorDao donorDao;
    private final DonationDao donationDao;

    public DonorService() {
        this.donorDao = new DonorDao();
        this.donationDao = new DonationDao();
    }

    public Donor saveDonor(Donor donor) {
        validateDonor(donor);
        
        if (donor.getId() == null) {
            logger.info("Creating new donor: {}", donor.getFullName());
            return donorDao.save(donor);
        } else {
            logger.info("Updating donor: {}", donor.getFullName());
            return donorDao.update(donor);
        }
    }

    public void deleteDonor(Long id) {
        donorDao.deleteById(id);
        logger.info("Deleted donor with ID: {}", id);
    }

    public Optional<Donor> findById(Long id) {
        return donorDao.findById(id);
    }

    public List<Donor> findAll() {
        return donorDao.findAll();
    }

    public List<Donor> findByBloodGroup(String bloodGroup) {
        return donorDao.findByBloodGroup(bloodGroup);
    }

    public List<Donor> findEligibleDonors() {
        return donorDao.findEligibleDonors();
    }

    public List<Donor> findEligibleByBloodGroup(String bloodGroup) {
        return donorDao.findEligibleByBloodGroup(bloodGroup);
    }

    public List<Donor> findDonorsReadyToDonate() {
        return donorDao.findReadyToDonate();
    }

    public List<Donor> search(String keyword) {
        return donorDao.search(keyword);
    }

    /**
     * Find compatible donors for a recipient blood group.
     */
    public List<Donor> findCompatibleDonors(String recipientBloodGroup) {
        var compatibleGroups = BloodCompatibility.getCompatibleDonorGroups(recipientBloodGroup);
        return donorDao.findAll().stream()
                .filter(d -> compatibleGroups.contains(d.getBloodGroup()))
                .filter(Donor::canDonate)
                .toList();
    }

    /**
     * Check if a donor is eligible to donate.
     */
    public DonorEligibility checkEligibility(Donor donor) {
        if (donor == null) {
            return new DonorEligibility(false, "Donor not found");
        }

        // Check if donor is marked as ineligible
        if (!donor.getIsEligible()) {
            return new DonorEligibility(false, "Donor is marked as ineligible");
        }

        // Check age
        if (donor.getDateOfBirth() != null) {
            int age = donor.getAge();
            if (age < MIN_AGE) {
                return new DonorEligibility(false, "Donor must be at least 18 years old");
            }
            if (age > MAX_AGE) {
                return new DonorEligibility(false, "Donor must be 65 years or younger");
            }
        }

        // Check weight
        if (donor.getWeightKg() != null && donor.getWeightKg() < MIN_WEIGHT_KG) {
            return new DonorEligibility(false, "Donor must weigh at least 50 kg");
        }

        // Check cooldown period
        if (donor.getLastDonationDate() != null) {
            LocalDate nextEligibleDate = donor.getLastDonationDate().plusDays(DONATION_COOLDOWN_DAYS);
            if (nextEligibleDate.isAfter(LocalDate.now())) {
                long daysRemaining = LocalDate.now().until(nextEligibleDate).getDays();
                return new DonorEligibility(false, 
                        String.format("Donor must wait %d more days before next donation", daysRemaining));
            }
        }

        return new DonorEligibility(true, "Donor is eligible to donate");
    }

    /**
     * Record a new donation.
     */
    public Donation recordDonation(Donor donor, Donation donation) {
        DonorEligibility eligibility = checkEligibility(donor);
        if (!eligibility.isEligible()) {
            throw new IllegalStateException(eligibility.getReason());
        }

        donation.setDonor(donor);
        donation.setBloodGroup(donor.getBloodGroup());
        donation.setDonationDate(LocalDate.now());
        donation.setStatus(Donation.DonationStatus.COMPLETED);

        Donation saved = donationDao.save(donation);

        // Update donor's last donation date and count
        donor.setLastDonationDate(LocalDate.now());
        donor.setTotalDonations(donor.getTotalDonations() + 1);
        donorDao.update(donor);

        logger.info("Recorded donation from donor: {} ({})", donor.getFullName(), donor.getBloodGroup());
        return saved;
    }

    public List<Donation> getDonationHistory(Long donorId) {
        return donationDao.findByDonorId(donorId);
    }

    private void validateDonor(Donor donor) {
        if (donor.getFullName() == null || donor.getFullName().trim().isEmpty()) {
            throw new IllegalArgumentException("Donor name is required");
        }
        if (!BloodCompatibility.isValidBloodGroup(donor.getBloodGroup())) {
            throw new IllegalArgumentException("Invalid blood group");
        }
    }

    public long getTotalDonorCount() {
        return donorDao.count();
    }

    public record DonorEligibility(boolean isEligible, String reason) {
        public boolean isEligible() { return isEligible; }
        public String getReason() { return reason; }
    }
}
