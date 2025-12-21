package com.bbms.dao;

import com.bbms.model.BloodRequest;
import java.util.List;

public class BloodRequestDao extends AbstractDao<BloodRequest, Long> {

    public List<BloodRequest> findByRecipientId(Long recipientId) {
        return executeQuery(
                "FROM BloodRequest WHERE recipient.id = :recipientId ORDER BY requestDate DESC",
                "recipientId", recipientId
        );
    }

    public List<BloodRequest> findPendingRequests() {
        return executeQuery(
                "FROM BloodRequest WHERE status = 'PENDING' OR status = 'APPROVED' ORDER BY priority DESC, requestDate ASC"
        );
    }

    public List<BloodRequest> findByStatus(BloodRequest.RequestStatus status) {
        return executeQuery(
                "FROM BloodRequest WHERE status = :status ORDER BY requestDate DESC",
                "status", status
        );
    }

    public List<BloodRequest> findEmergencyRequests() {
        return executeQuery(
                "FROM BloodRequest WHERE priority = 'EMERGENCY' AND (status = 'PENDING' OR status = 'APPROVED') ORDER BY requestDate ASC"
        );
    }

    public List<BloodRequest> findByBloodGroup(String bloodGroup) {
        return executeQuery(
                "FROM BloodRequest WHERE bloodGroup = :bloodGroup ORDER BY requestDate DESC",
                "bloodGroup", bloodGroup
        );
    }
}
