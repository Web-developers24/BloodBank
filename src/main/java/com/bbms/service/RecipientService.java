package com.bbms.service;

import com.bbms.dao.RecipientDao;
import com.bbms.dao.BloodRequestDao;
import com.bbms.dao.TransfusionDao;
import com.bbms.model.*;
import com.bbms.util.BloodCompatibility;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class RecipientService {

    private static final Logger logger = LogManager.getLogger(RecipientService.class);

    private final RecipientDao recipientDao;
    private final BloodRequestDao bloodRequestDao;
    private final TransfusionDao transfusionDao;
    private final BloodStockService bloodStockService;

    public RecipientService() {
        this.recipientDao = new RecipientDao();
        this.bloodRequestDao = new BloodRequestDao();
        this.transfusionDao = new TransfusionDao();
        this.bloodStockService = new BloodStockService();
    }

    public Recipient saveRecipient(Recipient recipient) {
        validateRecipient(recipient);
        
        if (recipient.getId() == null) {
            logger.info("Creating new recipient: {}", recipient.getFullName());
            return recipientDao.save(recipient);
        } else {
            logger.info("Updating recipient: {}", recipient.getFullName());
            return recipientDao.update(recipient);
        }
    }

    public void deleteRecipient(Long id) {
        recipientDao.deleteById(id);
        logger.info("Deleted recipient with ID: {}", id);
    }

    public Optional<Recipient> findById(Long id) {
        return recipientDao.findById(id);
    }

    public List<Recipient> findAll() {
        return recipientDao.findAll();
    }

    public List<Recipient> findByBloodGroup(String bloodGroup) {
        return recipientDao.findByBloodGroup(bloodGroup);
    }

    public List<Recipient> search(String keyword) {
        return recipientDao.search(keyword);
    }

    /**
     * Create a blood request for a recipient.
     */
    public BloodRequest createBloodRequest(Recipient recipient, String bloodGroup, 
                                            BloodStock.ComponentType componentType,
                                            int unitsNeeded, BloodRequest.Priority priority,
                                            LocalDate requiredByDate, String notes) {
        BloodRequest request = new BloodRequest();
        request.setRecipient(recipient);
        request.setBloodGroup(bloodGroup);
        request.setComponentType(componentType);
        request.setUnitsRequested(unitsNeeded);
        request.setPriority(priority);
        request.setRequestDate(LocalDate.now());
        request.setRequiredByDate(requiredByDate);
        request.setHospitalName(recipient.getHospitalName());
        request.setDoctorName(recipient.getDoctorName());
        request.setNotes(notes);
        request.setStatus(BloodRequest.RequestStatus.PENDING);

        BloodRequest saved = bloodRequestDao.save(request);
        logger.info("Created blood request for recipient: {} - {} units of {}", 
                recipient.getFullName(), unitsNeeded, bloodGroup);
        return saved;
    }

    /**
     * Get pending requests.
     */
    public List<BloodRequest> getPendingRequests() {
        return bloodRequestDao.findPendingRequests();
    }

    /**
     * Get emergency requests.
     */
    public List<BloodRequest> getEmergencyRequests() {
        return bloodRequestDao.findEmergencyRequests();
    }

    /**
     * Approve a blood request.
     */
    public void approveRequest(Long requestId) {
        Optional<BloodRequest> requestOpt = bloodRequestDao.findById(requestId);
        if (requestOpt.isPresent()) {
            BloodRequest request = requestOpt.get();
            request.setStatus(BloodRequest.RequestStatus.APPROVED);
            bloodRequestDao.update(request);
            logger.info("Approved blood request ID: {}", requestId);
        }
    }

    /**
     * Process a transfusion.
     */
    public Transfusion processTransfusion(BloodRequest request, BloodStock stock, 
                                           int units, String administeredBy) {
        // Validate compatibility
        if (!BloodCompatibility.isCompatible(request.getBloodGroup(), stock.getBloodGroup())) {
            throw new IllegalArgumentException("Blood types are not compatible");
        }

        // Check stock availability
        if (stock.getUnitsAvailable() < units) {
            throw new IllegalArgumentException("Insufficient stock available");
        }

        // Deduct from stock
        bloodStockService.deductStock(stock.getId(), units);

        // Create transfusion record
        Transfusion transfusion = new Transfusion();
        transfusion.setRecipient(request.getRecipient());
        transfusion.setBloodStock(stock);
        transfusion.setBloodRequest(request);
        transfusion.setBloodGroup(stock.getBloodGroup());
        transfusion.setComponentType(stock.getComponentType());
        transfusion.setUnitsTransfused(units);
        transfusion.setTransfusionDate(LocalDate.now());
        transfusion.setAdministeredBy(administeredBy);
        transfusion.setStatus(Transfusion.TransfusionStatus.COMPLETED);

        Transfusion saved = transfusionDao.save(transfusion);

        // Update request status
        request.setUnitsFulfilled(request.getUnitsFulfilled() + units);
        if (request.isFulfilled()) {
            request.setStatus(BloodRequest.RequestStatus.FULFILLED);
        } else {
            request.setStatus(BloodRequest.RequestStatus.PARTIALLY_FULFILLED);
        }
        bloodRequestDao.update(request);

        logger.info("Processed transfusion: {} units of {} for recipient {}",
                units, stock.getBloodGroup(), request.getRecipient().getFullName());
        return saved;
    }

    /**
     * Record a transfusion reaction.
     */
    public void recordReaction(Long transfusionId, String reactionDetails) {
        Optional<Transfusion> transfusionOpt = transfusionDao.findById(transfusionId);
        if (transfusionOpt.isPresent()) {
            Transfusion transfusion = transfusionOpt.get();
            transfusion.setReactionObserved(true);
            transfusion.setReactionDetails(reactionDetails);
            transfusionDao.update(transfusion);
            logger.warn("Recorded transfusion reaction for ID: {}", transfusionId);
        }
    }

    /**
     * Get transfusion history for a recipient.
     */
    public List<Transfusion> getTransfusionHistory(Long recipientId) {
        return transfusionDao.findByRecipientId(recipientId);
    }

    /**
     * Get blood request history for a recipient.
     */
    public List<BloodRequest> getRequestHistory(Long recipientId) {
        return bloodRequestDao.findByRecipientId(recipientId);
    }

    private void validateRecipient(Recipient recipient) {
        if (recipient.getFullName() == null || recipient.getFullName().trim().isEmpty()) {
            throw new IllegalArgumentException("Recipient name is required");
        }
        if (!BloodCompatibility.isValidBloodGroup(recipient.getBloodGroup())) {
            throw new IllegalArgumentException("Invalid blood group");
        }
    }

    public long getTotalRecipientCount() {
        return recipientDao.count();
    }
}
