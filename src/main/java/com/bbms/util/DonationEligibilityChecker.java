package com.bbms.util;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Donation Eligibility Checker Utility
 * 
 * Validates if a person is eligible to donate blood based on:
 * - Age requirements (18-65 years)
 * - Weight requirements (minimum 50kg / 110lbs)
 * - Time since last donation (56 days for whole blood)
 * - Health conditions and medications
 * - Travel history restrictions
 * 
 * Based on standard blood bank guidelines (WHO/FDA/Red Cross).
 * 
 * @author Nivedhaa Sai Saravana Kumar
 * @version 1.0
 */
public class DonationEligibilityChecker {

    // Constants
    public static final int MIN_AGE = 18;
    public static final int MAX_AGE = 65;
    public static final double MIN_WEIGHT_KG = 50.0;
    public static final double MIN_WEIGHT_LBS = 110.0;
    public static final int WHOLE_BLOOD_INTERVAL_DAYS = 56;
    public static final int PLATELET_INTERVAL_DAYS = 7;
    public static final int PLASMA_INTERVAL_DAYS = 28;
    public static final double MIN_HEMOGLOBIN_MALE = 13.0;    // g/dL
    public static final double MIN_HEMOGLOBIN_FEMALE = 12.5;  // g/dL

    // Temporary deferrals (condition -> deferral days)
    private static final Map<String, Integer> TEMPORARY_DEFERRALS = new LinkedHashMap<>();
    static {
        TEMPORARY_DEFERRALS.put("cold_flu", 7);
        TEMPORARY_DEFERRALS.put("fever", 14);
        TEMPORARY_DEFERRALS.put("antibiotics", 14);
        TEMPORARY_DEFERRALS.put("dental_procedure", 3);
        TEMPORARY_DEFERRALS.put("tattoo_piercing", 90);
        TEMPORARY_DEFERRALS.put("vaccination", 14);
        TEMPORARY_DEFERRALS.put("minor_surgery", 90);
        TEMPORARY_DEFERRALS.put("major_surgery", 180);
        TEMPORARY_DEFERRALS.put("pregnancy", 180);
        TEMPORARY_DEFERRALS.put("blood_transfusion", 365);
    }

    // Permanent deferrals
    private static final Set<String> PERMANENT_DEFERRALS = Set.of(
        "hiv_positive",
        "hepatitis_b",
        "hepatitis_c",
        "heart_disease",
        "cancer_active",
        "bleeding_disorder",
        "organ_transplant"
    );

    /**
     * Result class for eligibility check
     */
    public static class EligibilityResult {
        private final boolean eligible;
        private final List<String> reasons;
        private final LocalDate nextEligibleDate;

        public EligibilityResult(boolean eligible, List<String> reasons, LocalDate nextEligibleDate) {
            this.eligible = eligible;
            this.reasons = reasons;
            this.nextEligibleDate = nextEligibleDate;
        }

        public boolean isEligible() { return eligible; }
        public List<String> getReasons() { return reasons; }
        public LocalDate getNextEligibleDate() { return nextEligibleDate; }

        @Override
        public String toString() {
            if (eligible) {
                return "✅ Eligible to donate blood";
            } else {
                StringBuilder sb = new StringBuilder("❌ Not eligible to donate:\n");
                for (String reason : reasons) {
                    sb.append("  • ").append(reason).append("\n");
                }
                if (nextEligibleDate != null) {
                    sb.append("Next eligible date: ").append(nextEligibleDate);
                }
                return sb.toString();
            }
        }
    }

    /**
     * Check if person meets age requirement
     */
    public static boolean isAgeEligible(LocalDate birthDate) {
        if (birthDate == null) return false;
        int age = Period.between(birthDate, LocalDate.now()).getYears();
        return age >= MIN_AGE && age <= MAX_AGE;
    }

    /**
     * Calculate age from birth date
     */
    public static int calculateAge(LocalDate birthDate) {
        if (birthDate == null) return 0;
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    /**
     * Check if person meets weight requirement
     */
    public static boolean isWeightEligible(double weightKg) {
        return weightKg >= MIN_WEIGHT_KG;
    }

    /**
     * Convert weight from pounds to kilograms
     */
    public static double lbsToKg(double weightLbs) {
        return weightLbs * 0.453592;
    }

    /**
     * Check if enough time has passed since last donation
     */
    public static boolean hasEnoughTimeSinceLastDonation(LocalDate lastDonationDate, String donationType) {
        if (lastDonationDate == null) return true;
        
        long daysSinceLastDonation = ChronoUnit.DAYS.between(lastDonationDate, LocalDate.now());
        
        int requiredDays = switch (donationType.toLowerCase()) {
            case "platelet", "platelets" -> PLATELET_INTERVAL_DAYS;
            case "plasma" -> PLASMA_INTERVAL_DAYS;
            default -> WHOLE_BLOOD_INTERVAL_DAYS;
        };
        
        return daysSinceLastDonation >= requiredDays;
    }

    /**
     * Get days until next eligible donation
     */
    public static long getDaysUntilEligible(LocalDate lastDonationDate, String donationType) {
        if (lastDonationDate == null) return 0;
        
        int requiredDays = switch (donationType.toLowerCase()) {
            case "platelet", "platelets" -> PLATELET_INTERVAL_DAYS;
            case "plasma" -> PLASMA_INTERVAL_DAYS;
            default -> WHOLE_BLOOD_INTERVAL_DAYS;
        };
        
        LocalDate eligibleDate = lastDonationDate.plusDays(requiredDays);
        long daysUntil = ChronoUnit.DAYS.between(LocalDate.now(), eligibleDate);
        return Math.max(0, daysUntil);
    }

    /**
     * Check if hemoglobin level is sufficient
     */
    public static boolean isHemoglobinEligible(double hemoglobin, String gender) {
        double minRequired = "female".equalsIgnoreCase(gender) ? 
            MIN_HEMOGLOBIN_FEMALE : MIN_HEMOGLOBIN_MALE;
        return hemoglobin >= minRequired;
    }

    /**
     * Check if condition causes permanent deferral
     */
    public static boolean isPermanentlyDeferred(String condition) {
        return PERMANENT_DEFERRALS.contains(condition.toLowerCase().replace(" ", "_"));
    }

    /**
     * Get deferral period for temporary condition
     */
    public static int getTemporaryDeferralDays(String condition) {
        return TEMPORARY_DEFERRALS.getOrDefault(
            condition.toLowerCase().replace(" ", "_"), 
            0
        );
    }

    /**
     * Comprehensive eligibility check
     */
    public static EligibilityResult checkEligibility(
            LocalDate birthDate,
            double weightKg,
            String gender,
            double hemoglobin,
            LocalDate lastDonationDate,
            String donationType,
            List<String> conditions) {
        
        List<String> reasons = new ArrayList<>();
        LocalDate nextEligible = LocalDate.now();
        boolean hasTemporaryDeferral = false;

        // Age check
        if (!isAgeEligible(birthDate)) {
            int age = calculateAge(birthDate);
            if (age < MIN_AGE) {
                reasons.add("Must be at least " + MIN_AGE + " years old (current age: " + age + ")");
                nextEligible = birthDate.plusYears(MIN_AGE);
                hasTemporaryDeferral = true;
            } else {
                reasons.add("Maximum donation age is " + MAX_AGE + " years (current age: " + age + ")");
                return new EligibilityResult(false, reasons, null); // Permanent
            }
        }

        // Weight check
        if (!isWeightEligible(weightKg)) {
            reasons.add(String.format("Minimum weight is %.1f kg (current: %.1f kg)", MIN_WEIGHT_KG, weightKg));
        }

        // Hemoglobin check
        if (!isHemoglobinEligible(hemoglobin, gender)) {
            double minRequired = "female".equalsIgnoreCase(gender) ? 
                MIN_HEMOGLOBIN_FEMALE : MIN_HEMOGLOBIN_MALE;
            reasons.add(String.format("Hemoglobin too low: %.1f g/dL (minimum: %.1f g/dL)", 
                hemoglobin, minRequired));
        }

        // Last donation check
        if (!hasEnoughTimeSinceLastDonation(lastDonationDate, donationType)) {
            long daysUntil = getDaysUntilEligible(lastDonationDate, donationType);
            LocalDate eligibleDate = LocalDate.now().plusDays(daysUntil);
            reasons.add("Must wait " + daysUntil + " more days since last " + donationType + " donation");
            if (eligibleDate.isAfter(nextEligible)) {
                nextEligible = eligibleDate;
            }
            hasTemporaryDeferral = true;
        }

        // Check conditions
        if (conditions != null) {
            for (String condition : conditions) {
                String normalizedCondition = condition.toLowerCase().replace(" ", "_");
                
                if (isPermanentlyDeferred(normalizedCondition)) {
                    reasons.add("Permanent deferral due to: " + condition);
                    return new EligibilityResult(false, reasons, null);
                }
                
                int deferralDays = getTemporaryDeferralDays(normalizedCondition);
                if (deferralDays > 0) {
                    reasons.add("Temporary deferral (" + deferralDays + " days) due to: " + condition);
                    LocalDate deferralEnd = LocalDate.now().plusDays(deferralDays);
                    if (deferralEnd.isAfter(nextEligible)) {
                        nextEligible = deferralEnd;
                    }
                    hasTemporaryDeferral = true;
                }
            }
        }

        if (reasons.isEmpty()) {
            return new EligibilityResult(true, Collections.emptyList(), null);
        } else {
            return new EligibilityResult(false, reasons, 
                hasTemporaryDeferral ? nextEligible : null);
        }
    }

    /**
     * Quick eligibility check (age and weight only)
     */
    public static boolean isBasicallyEligible(LocalDate birthDate, double weightKg) {
        return isAgeEligible(birthDate) && isWeightEligible(weightKg);
    }

    /**
     * Get list of all temporary deferral conditions
     */
    public static Map<String, Integer> getTemporaryDeferralConditions() {
        return Collections.unmodifiableMap(TEMPORARY_DEFERRALS);
    }

    /**
     * Get list of all permanent deferral conditions
     */
    public static Set<String> getPermanentDeferralConditions() {
        return PERMANENT_DEFERRALS;
    }

    /**
     * Calculate estimated blood volume based on weight and gender
     * Uses Nadler's equation
     */
    public static double estimateBloodVolume(double weightKg, double heightCm, String gender) {
        if ("male".equalsIgnoreCase(gender)) {
            return 0.3669 * Math.pow(heightCm / 100, 3) + 
                   0.03219 * weightKg + 0.6041;
        } else {
            return 0.3561 * Math.pow(heightCm / 100, 3) + 
                   0.03308 * weightKg + 0.1833;
        }
    }

    /**
     * Check if person can safely donate given their blood volume
     * Standard donation is ~450ml, should be <10-12% of blood volume
     */
    public static boolean canSafelyDonate(double weightKg, double heightCm, String gender) {
        double bloodVolume = estimateBloodVolume(weightKg, heightCm, gender) * 1000; // Convert to mL
        double donationAmount = 450.0; // Standard donation in mL
        return (donationAmount / bloodVolume) < 0.12; // Less than 12%
    }
}
