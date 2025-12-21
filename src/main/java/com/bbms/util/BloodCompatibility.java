package com.bbms.util;

import java.util.*;

/**
 * Blood type compatibility checker.
 * Handles matching donors to recipients based on blood group compatibility.
 */
public class BloodCompatibility {

    private static final Map<String, Set<String>> COMPATIBILITY_MAP = new HashMap<>();

    static {
        // Recipient -> Set of compatible donor blood groups
        COMPATIBILITY_MAP.put("O-", Set.of("O-"));
        COMPATIBILITY_MAP.put("O+", Set.of("O-", "O+"));
        COMPATIBILITY_MAP.put("A-", Set.of("O-", "A-"));
        COMPATIBILITY_MAP.put("A+", Set.of("O-", "O+", "A-", "A+"));
        COMPATIBILITY_MAP.put("B-", Set.of("O-", "B-"));
        COMPATIBILITY_MAP.put("B+", Set.of("O-", "O+", "B-", "B+"));
        COMPATIBILITY_MAP.put("AB-", Set.of("O-", "A-", "B-", "AB-"));
        COMPATIBILITY_MAP.put("AB+", Set.of("O-", "O+", "A-", "A+", "B-", "B+", "AB-", "AB+"));
    }

    public static final String[] ALL_BLOOD_GROUPS = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};

    /**
     * Check if a donor blood group is compatible with a recipient blood group.
     */
    public static boolean isCompatible(String recipientBloodGroup, String donorBloodGroup) {
        if (recipientBloodGroup == null || donorBloodGroup == null) {
            return false;
        }
        Set<String> compatibleDonors = COMPATIBILITY_MAP.get(recipientBloodGroup.toUpperCase());
        return compatibleDonors != null && compatibleDonors.contains(donorBloodGroup.toUpperCase());
    }

    /**
     * Get all compatible donor blood groups for a recipient.
     */
    public static Set<String> getCompatibleDonorGroups(String recipientBloodGroup) {
        if (recipientBloodGroup == null) {
            return Collections.emptySet();
        }
        return COMPATIBILITY_MAP.getOrDefault(recipientBloodGroup.toUpperCase(), Collections.emptySet());
    }

    /**
     * Get all recipient blood groups that can receive from a donor.
     */
    public static Set<String> getCompatibleRecipientGroups(String donorBloodGroup) {
        if (donorBloodGroup == null) {
            return Collections.emptySet();
        }
        String upperDonor = donorBloodGroup.toUpperCase();
        Set<String> compatibleRecipients = new HashSet<>();
        
        for (Map.Entry<String, Set<String>> entry : COMPATIBILITY_MAP.entrySet()) {
            if (entry.getValue().contains(upperDonor)) {
                compatibleRecipients.add(entry.getKey());
            }
        }
        return compatibleRecipients;
    }

    /**
     * Check if a blood group is valid.
     */
    public static boolean isValidBloodGroup(String bloodGroup) {
        if (bloodGroup == null) return false;
        return COMPATIBILITY_MAP.containsKey(bloodGroup.toUpperCase());
    }

    /**
     * Get universal donor blood group.
     */
    public static String getUniversalDonor() {
        return "O-";
    }

    /**
     * Get universal recipient blood group.
     */
    public static String getUniversalRecipient() {
        return "AB+";
    }

    /**
     * Check if a blood group is a universal donor (O-).
     */
    public static boolean isUniversalDonor(String bloodGroup) {
        return "O-".equalsIgnoreCase(bloodGroup);
    }

    /**
     * Check if a blood group is a universal recipient (AB+).
     */
    public static boolean isUniversalRecipient(String bloodGroup) {
        return "AB+".equalsIgnoreCase(bloodGroup);
    }

    /**
     * Get priority score for blood matching.
     * Higher score = better match (same blood type preferred).
     */
    public static int getMatchPriority(String recipientBloodGroup, String donorBloodGroup) {
        if (!isCompatible(recipientBloodGroup, donorBloodGroup)) {
            return -1;
        }
        // Same blood type = highest priority
        if (recipientBloodGroup.equalsIgnoreCase(donorBloodGroup)) {
            return 100;
        }
        // Same ABO type, different Rh = high priority
        String recipientABO = recipientBloodGroup.substring(0, recipientBloodGroup.length() - 1);
        String donorABO = donorBloodGroup.substring(0, donorBloodGroup.length() - 1);
        if (recipientABO.equals(donorABO)) {
            return 75;
        }
        // O type donors = medium priority
        if (donorABO.equals("O")) {
            return 50;
        }
        // Other compatible = low priority
        return 25;
    }
}
