package com.bbms.util;

import java.util.*;

/**
 * Blood Type Compatibility Checker Utility
 * 
 * Provides comprehensive blood type compatibility checking for:
 * - Red Blood Cell (RBC) transfusions
 * - Plasma transfusions
 * - Platelet transfusions
 * 
 * Based on ABO and Rh blood group systems.
 * 
 * @author Nivedhaa Sai Saravana Kumar
 * @version 1.0
 */
public class BloodTypeCompatibility {

    // Valid blood types
    private static final Set<String> VALID_BLOOD_TYPES = Set.of(
        "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"
    );

    // RBC Compatibility: donor -> list of compatible recipients
    private static final Map<String, List<String>> RBC_COMPATIBILITY = Map.of(
        "O-",  List.of("O-", "O+", "A-", "A+", "B-", "B+", "AB-", "AB+"),
        "O+",  List.of("O+", "A+", "B+", "AB+"),
        "A-",  List.of("A-", "A+", "AB-", "AB+"),
        "A+",  List.of("A+", "AB+"),
        "B-",  List.of("B-", "B+", "AB-", "AB+"),
        "B+",  List.of("B+", "AB+"),
        "AB-", List.of("AB-", "AB+"),
        "AB+", List.of("AB+")
    );

    // Plasma Compatibility: donor -> list of compatible recipients (reverse of RBC)
    private static final Map<String, List<String>> PLASMA_COMPATIBILITY = Map.of(
        "AB+", List.of("O-", "O+", "A-", "A+", "B-", "B+", "AB-", "AB+"),
        "AB-", List.of("O-", "O+", "A-", "A+", "B-", "B+", "AB-", "AB+"),
        "A+",  List.of("O+", "A+"),
        "A-",  List.of("O-", "O+", "A-", "A+"),
        "B+",  List.of("O+", "B+"),
        "B-",  List.of("O-", "O+", "B-", "B+"),
        "O+",  List.of("O+"),
        "O-",  List.of("O-", "O+")
    );

    /**
     * Check if donor blood can be given to recipient for RBC transfusion
     * 
     * @param donorType Blood type of donor
     * @param recipientType Blood type of recipient
     * @return true if compatible, false otherwise
     */
    public static boolean canDonateRBCTo(String donorType, String recipientType) {
        validateBloodType(donorType);
        validateBloodType(recipientType);
        
        List<String> compatibleRecipients = RBC_COMPATIBILITY.get(donorType.toUpperCase());
        return compatibleRecipients != null && 
               compatibleRecipients.contains(recipientType.toUpperCase());
    }

    /**
     * Check if donor plasma can be given to recipient
     * 
     * @param donorType Blood type of donor
     * @param recipientType Blood type of recipient
     * @return true if compatible, false otherwise
     */
    public static boolean canDonatePlasmaTo(String donorType, String recipientType) {
        validateBloodType(donorType);
        validateBloodType(recipientType);
        
        List<String> compatibleRecipients = PLASMA_COMPATIBILITY.get(donorType.toUpperCase());
        return compatibleRecipients != null && 
               compatibleRecipients.contains(recipientType.toUpperCase());
    }

    /**
     * Get all blood types that can donate RBC to given recipient
     * 
     * @param recipientType Blood type of recipient
     * @return List of compatible donor blood types
     */
    public static List<String> getCompatibleRBCDonors(String recipientType) {
        validateBloodType(recipientType);
        
        List<String> compatibleDonors = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : RBC_COMPATIBILITY.entrySet()) {
            if (entry.getValue().contains(recipientType.toUpperCase())) {
                compatibleDonors.add(entry.getKey());
            }
        }
        return compatibleDonors;
    }

    /**
     * Get all blood types that can receive RBC from given donor
     * 
     * @param donorType Blood type of donor
     * @return List of compatible recipient blood types
     */
    public static List<String> getCompatibleRBCRecipients(String donorType) {
        validateBloodType(donorType);
        return new ArrayList<>(RBC_COMPATIBILITY.get(donorType.toUpperCase()));
    }

    /**
     * Get all blood types that can donate plasma to given recipient
     * 
     * @param recipientType Blood type of recipient
     * @return List of compatible donor blood types
     */
    public static List<String> getCompatiblePlasmaDonors(String recipientType) {
        validateBloodType(recipientType);
        
        List<String> compatibleDonors = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : PLASMA_COMPATIBILITY.entrySet()) {
            if (entry.getValue().contains(recipientType.toUpperCase())) {
                compatibleDonors.add(entry.getKey());
            }
        }
        return compatibleDonors;
    }

    /**
     * Check if blood type is universal donor (O-)
     * 
     * @param bloodType Blood type to check
     * @return true if universal donor
     */
    public static boolean isUniversalDonor(String bloodType) {
        validateBloodType(bloodType);
        return "O-".equalsIgnoreCase(bloodType);
    }

    /**
     * Check if blood type is universal recipient (AB+)
     * 
     * @param bloodType Blood type to check
     * @return true if universal recipient
     */
    public static boolean isUniversalRecipient(String bloodType) {
        validateBloodType(bloodType);
        return "AB+".equalsIgnoreCase(bloodType);
    }

    /**
     * Check if blood type is universal plasma donor (AB)
     * 
     * @param bloodType Blood type to check
     * @return true if universal plasma donor
     */
    public static boolean isUniversalPlasmaDonor(String bloodType) {
        validateBloodType(bloodType);
        return bloodType.toUpperCase().startsWith("AB");
    }

    /**
     * Get Rh factor from blood type
     * 
     * @param bloodType Blood type
     * @return "+" or "-"
     */
    public static String getRhFactor(String bloodType) {
        validateBloodType(bloodType);
        return bloodType.endsWith("+") ? "+" : "-";
    }

    /**
     * Get ABO group from blood type
     * 
     * @param bloodType Blood type
     * @return "A", "B", "AB", or "O"
     */
    public static String getABOGroup(String bloodType) {
        validateBloodType(bloodType);
        return bloodType.toUpperCase().replace("+", "").replace("-", "");
    }

    /**
     * Check if blood type is Rh positive
     * 
     * @param bloodType Blood type to check
     * @return true if Rh positive
     */
    public static boolean isRhPositive(String bloodType) {
        validateBloodType(bloodType);
        return bloodType.endsWith("+");
    }

    /**
     * Validate blood type format
     * 
     * @param bloodType Blood type to validate
     * @throws IllegalArgumentException if invalid
     */
    public static void validateBloodType(String bloodType) {
        if (bloodType == null || !VALID_BLOOD_TYPES.contains(bloodType.toUpperCase())) {
            throw new IllegalArgumentException(
                "Invalid blood type: " + bloodType + 
                ". Valid types are: " + VALID_BLOOD_TYPES
            );
        }
    }

    /**
     * Check if blood type string is valid
     * 
     * @param bloodType Blood type to check
     * @return true if valid format
     */
    public static boolean isValidBloodType(String bloodType) {
        return bloodType != null && VALID_BLOOD_TYPES.contains(bloodType.toUpperCase());
    }

    /**
     * Get all valid blood types
     * 
     * @return Set of valid blood type strings
     */
    public static Set<String> getAllBloodTypes() {
        return VALID_BLOOD_TYPES;
    }

    /**
     * Get compatibility summary for a blood type
     * 
     * @param bloodType Blood type to check
     * @return Formatted string with compatibility info
     */
    public static String getCompatibilitySummary(String bloodType) {
        validateBloodType(bloodType);
        
        StringBuilder sb = new StringBuilder();
        sb.append("Blood Type: ").append(bloodType.toUpperCase()).append("\n");
        sb.append("ABO Group: ").append(getABOGroup(bloodType)).append("\n");
        sb.append("Rh Factor: ").append(getRhFactor(bloodType)).append("\n");
        sb.append("\n");
        sb.append("Can donate RBC to: ").append(getCompatibleRBCRecipients(bloodType)).append("\n");
        sb.append("Can receive RBC from: ").append(getCompatibleRBCDonors(bloodType)).append("\n");
        sb.append("\n");
        
        if (isUniversalDonor(bloodType)) {
            sb.append("★ Universal RBC Donor\n");
        }
        if (isUniversalRecipient(bloodType)) {
            sb.append("★ Universal RBC Recipient\n");
        }
        if (isUniversalPlasmaDonor(bloodType)) {
            sb.append("★ Universal Plasma Donor\n");
        }
        
        return sb.toString();
    }
}
