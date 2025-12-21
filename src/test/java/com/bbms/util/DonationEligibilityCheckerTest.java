package com.bbms.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DonationEligibilityChecker utility class
 * 
 * @author Nivedhaa Sai Saravana Kumar
 */
class DonationEligibilityCheckerTest {

    @Test
    @DisplayName("Person aged 25 should be eligible by age")
    void testAgeEligible25() {
        LocalDate birthDate = LocalDate.now().minusYears(25);
        assertTrue(DonationEligibilityChecker.isAgeEligible(birthDate));
    }

    @Test
    @DisplayName("Person aged 17 should not be eligible by age")
    void testAgeIneligible17() {
        LocalDate birthDate = LocalDate.now().minusYears(17);
        assertFalse(DonationEligibilityChecker.isAgeEligible(birthDate));
    }

    @Test
    @DisplayName("Person aged 70 should not be eligible by age")
    void testAgeIneligible70() {
        LocalDate birthDate = LocalDate.now().minusYears(70);
        assertFalse(DonationEligibilityChecker.isAgeEligible(birthDate));
    }

    @ParameterizedTest
    @CsvSource({
        "18, true",
        "25, true",
        "45, true",
        "65, true",
        "17, false",
        "66, false",
        "80, false"
    })
    @DisplayName("Age boundary tests")
    void testAgeBoundaries(int age, boolean expected) {
        LocalDate birthDate = LocalDate.now().minusYears(age);
        assertEquals(expected, DonationEligibilityChecker.isAgeEligible(birthDate));
    }

    @Test
    @DisplayName("Weight 50kg should be eligible")
    void testWeightEligible50kg() {
        assertTrue(DonationEligibilityChecker.isWeightEligible(50.0));
    }

    @Test
    @DisplayName("Weight 49kg should not be eligible")
    void testWeightIneligible49kg() {
        assertFalse(DonationEligibilityChecker.isWeightEligible(49.0));
    }

    @ParameterizedTest
    @CsvSource({
        "50.0, true",
        "55.0, true",
        "75.0, true",
        "49.9, false",
        "45.0, false",
        "30.0, false"
    })
    @DisplayName("Weight boundary tests")
    void testWeightBoundaries(double weight, boolean expected) {
        assertEquals(expected, DonationEligibilityChecker.isWeightEligible(weight));
    }

    @Test
    @DisplayName("Convert 110 lbs to kg correctly")
    void testLbsToKg() {
        double kg = DonationEligibilityChecker.lbsToKg(110);
        assertTrue(kg >= 49.8 && kg <= 50.0);
    }

    @Test
    @DisplayName("First time donor should be eligible (no last donation)")
    void testFirstTimeDonor() {
        assertTrue(DonationEligibilityChecker.hasEnoughTimeSinceLastDonation(null, "whole_blood"));
    }

    @Test
    @DisplayName("Donation 60 days ago should be eligible for whole blood")
    void testDonation60DaysAgo() {
        LocalDate lastDonation = LocalDate.now().minusDays(60);
        assertTrue(DonationEligibilityChecker.hasEnoughTimeSinceLastDonation(lastDonation, "whole_blood"));
    }

    @Test
    @DisplayName("Donation 30 days ago should not be eligible for whole blood")
    void testDonation30DaysAgo() {
        LocalDate lastDonation = LocalDate.now().minusDays(30);
        assertFalse(DonationEligibilityChecker.hasEnoughTimeSinceLastDonation(lastDonation, "whole_blood"));
    }

    @Test
    @DisplayName("Donation 10 days ago should be eligible for platelet")
    void testPlateletDonation10DaysAgo() {
        LocalDate lastDonation = LocalDate.now().minusDays(10);
        assertTrue(DonationEligibilityChecker.hasEnoughTimeSinceLastDonation(lastDonation, "platelet"));
    }

    @Test
    @DisplayName("HIV positive should be permanently deferred")
    void testPermanentDeferralHIV() {
        assertTrue(DonationEligibilityChecker.isPermanentlyDeferred("hiv_positive"));
    }

    @Test
    @DisplayName("Hepatitis B should be permanently deferred")
    void testPermanentDeferralHepB() {
        assertTrue(DonationEligibilityChecker.isPermanentlyDeferred("hepatitis_b"));
    }

    @Test
    @DisplayName("Cold/flu should have 7 day deferral")
    void testTemporaryDeferralCold() {
        assertEquals(7, DonationEligibilityChecker.getTemporaryDeferralDays("cold_flu"));
    }

    @Test
    @DisplayName("Tattoo should have 90 day deferral")
    void testTemporaryDeferralTattoo() {
        assertEquals(90, DonationEligibilityChecker.getTemporaryDeferralDays("tattoo_piercing"));
    }

    @Test
    @DisplayName("Male hemoglobin 13.5 should be eligible")
    void testMaleHemoglobinEligible() {
        assertTrue(DonationEligibilityChecker.isHemoglobinEligible(13.5, "male"));
    }

    @Test
    @DisplayName("Male hemoglobin 12.0 should not be eligible")
    void testMaleHemoglobinIneligible() {
        assertFalse(DonationEligibilityChecker.isHemoglobinEligible(12.0, "male"));
    }

    @Test
    @DisplayName("Female hemoglobin 12.5 should be eligible")
    void testFemaleHemoglobinEligible() {
        assertTrue(DonationEligibilityChecker.isHemoglobinEligible(12.5, "female"));
    }

    @Test
    @DisplayName("Female hemoglobin 12.0 should not be eligible")
    void testFemaleHemoglobinIneligible() {
        assertFalse(DonationEligibilityChecker.isHemoglobinEligible(12.0, "female"));
    }

    @Test
    @DisplayName("Comprehensive check - fully eligible donor")
    void testFullyEligibleDonor() {
        LocalDate birthDate = LocalDate.now().minusYears(30);
        double weight = 70.0;
        String gender = "male";
        double hemoglobin = 14.0;
        LocalDate lastDonation = LocalDate.now().minusDays(60);
        
        DonationEligibilityChecker.EligibilityResult result = 
            DonationEligibilityChecker.checkEligibility(
                birthDate, weight, gender, hemoglobin, lastDonation, "whole_blood", null);
        
        assertTrue(result.isEligible());
        assertTrue(result.getReasons().isEmpty());
    }

    @Test
    @DisplayName("Comprehensive check - underage donor")
    void testUnderageDonor() {
        LocalDate birthDate = LocalDate.now().minusYears(16);
        
        DonationEligibilityChecker.EligibilityResult result = 
            DonationEligibilityChecker.checkEligibility(
                birthDate, 60.0, "male", 14.0, null, "whole_blood", null);
        
        assertFalse(result.isEligible());
        assertTrue(result.getReasons().stream().anyMatch(r -> r.contains("18")));
        assertNotNull(result.getNextEligibleDate());
    }

    @Test
    @DisplayName("Comprehensive check - permanent deferral")
    void testPermanentDeferralCheck() {
        LocalDate birthDate = LocalDate.now().minusYears(30);
        List<String> conditions = Arrays.asList("hiv_positive");
        
        DonationEligibilityChecker.EligibilityResult result = 
            DonationEligibilityChecker.checkEligibility(
                birthDate, 70.0, "male", 14.0, null, "whole_blood", conditions);
        
        assertFalse(result.isEligible());
        assertNull(result.getNextEligibleDate()); // No next date for permanent
    }

    @Test
    @DisplayName("Comprehensive check - temporary deferral")
    void testTemporaryDeferralCheck() {
        LocalDate birthDate = LocalDate.now().minusYears(30);
        List<String> conditions = Arrays.asList("cold_flu");
        
        DonationEligibilityChecker.EligibilityResult result = 
            DonationEligibilityChecker.checkEligibility(
                birthDate, 70.0, "male", 14.0, null, "whole_blood", conditions);
        
        assertFalse(result.isEligible());
        assertNotNull(result.getNextEligibleDate());
    }

    @Test
    @DisplayName("Calculate age correctly")
    void testCalculateAge() {
        LocalDate birthDate = LocalDate.now().minusYears(25).minusDays(100);
        assertEquals(25, DonationEligibilityChecker.calculateAge(birthDate));
    }

    @Test
    @DisplayName("Get days until eligible")
    void testGetDaysUntilEligible() {
        LocalDate lastDonation = LocalDate.now().minusDays(30);
        long daysUntil = DonationEligibilityChecker.getDaysUntilEligible(lastDonation, "whole_blood");
        assertEquals(26, daysUntil); // 56 - 30 = 26
    }

    @Test
    @DisplayName("Blood volume estimation returns positive value")
    void testBloodVolumeEstimation() {
        double volume = DonationEligibilityChecker.estimateBloodVolume(70, 175, "male");
        assertTrue(volume > 0);
        assertTrue(volume < 10); // Should be in liters, typically 4-6L
    }

    @Test
    @DisplayName("70kg 175cm male can safely donate")
    void testCanSafelyDonate() {
        assertTrue(DonationEligibilityChecker.canSafelyDonate(70, 175, "male"));
    }

    @Test
    @DisplayName("Basic eligibility check")
    void testBasicEligibility() {
        LocalDate birthDate = LocalDate.now().minusYears(25);
        assertTrue(DonationEligibilityChecker.isBasicallyEligible(birthDate, 60.0));
        assertFalse(DonationEligibilityChecker.isBasicallyEligible(birthDate, 45.0));
    }

    @Test
    @DisplayName("Get all temporary deferral conditions")
    void testGetTemporaryDeferralConditions() {
        assertFalse(DonationEligibilityChecker.getTemporaryDeferralConditions().isEmpty());
        assertTrue(DonationEligibilityChecker.getTemporaryDeferralConditions().containsKey("cold_flu"));
    }

    @Test
    @DisplayName("Get all permanent deferral conditions")
    void testGetPermanentDeferralConditions() {
        assertFalse(DonationEligibilityChecker.getPermanentDeferralConditions().isEmpty());
        assertTrue(DonationEligibilityChecker.getPermanentDeferralConditions().contains("hiv_positive"));
    }
}
