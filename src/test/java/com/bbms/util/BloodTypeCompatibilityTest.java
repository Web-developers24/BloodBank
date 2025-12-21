package com.bbms.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for BloodTypeCompatibility utility class
 * 
 * @author Nivedhaa Sai Saravana Kumar
 */
class BloodTypeCompatibilityTest {

    @Test
    @DisplayName("O- is universal donor - can donate to all blood types")
    void testUniversalDonor() {
        String[] allTypes = {"O-", "O+", "A-", "A+", "B-", "B+", "AB-", "AB+"};
        
        for (String recipient : allTypes) {
            assertTrue(BloodTypeCompatibility.canDonateRBCTo("O-", recipient),
                "O- should be able to donate to " + recipient);
        }
    }

    @Test
    @DisplayName("AB+ is universal recipient - can receive from all blood types")
    void testUniversalRecipient() {
        String[] allTypes = {"O-", "O+", "A-", "A+", "B-", "B+", "AB-", "AB+"};
        
        for (String donor : allTypes) {
            assertTrue(BloodTypeCompatibility.canDonateRBCTo(donor, "AB+"),
                donor + " should be able to donate to AB+");
        }
    }

    @ParameterizedTest
    @CsvSource({
        "A+, A+, true",
        "A+, AB+, true",
        "A+, O+, false",
        "A+, B+, false",
        "B+, B+, true",
        "B+, AB+, true",
        "B+, A+, false",
        "O+, O+, true",
        "O+, A+, true",
        "O+, B+, true",
        "O+, AB+, true",
        "O+, O-, false",
        "A-, A-, true",
        "A-, A+, true",
        "A-, AB-, true",
        "A-, AB+, true"
    })
    @DisplayName("RBC compatibility check")
    void testRBCCompatibility(String donor, String recipient, boolean expected) {
        assertEquals(expected, BloodTypeCompatibility.canDonateRBCTo(donor, recipient));
    }

    @Test
    @DisplayName("AB is universal plasma donor")
    void testUniversalPlasmaDonor() {
        assertTrue(BloodTypeCompatibility.isUniversalPlasmaDonor("AB+"));
        assertTrue(BloodTypeCompatibility.isUniversalPlasmaDonor("AB-"));
        assertFalse(BloodTypeCompatibility.isUniversalPlasmaDonor("A+"));
        assertFalse(BloodTypeCompatibility.isUniversalPlasmaDonor("O-"));
    }

    @Test
    @DisplayName("Get compatible RBC donors for A+")
    void testGetCompatibleDonorsForAPositive() {
        List<String> donors = BloodTypeCompatibility.getCompatibleRBCDonors("A+");
        
        assertTrue(donors.contains("A+"));
        assertTrue(donors.contains("A-"));
        assertTrue(donors.contains("O+"));
        assertTrue(donors.contains("O-"));
        assertFalse(donors.contains("B+"));
        assertFalse(donors.contains("AB+"));
    }

    @Test
    @DisplayName("Get compatible RBC recipients for O+")
    void testGetCompatibleRecipientsForOPositive() {
        List<String> recipients = BloodTypeCompatibility.getCompatibleRBCRecipients("O+");
        
        assertTrue(recipients.contains("O+"));
        assertTrue(recipients.contains("A+"));
        assertTrue(recipients.contains("B+"));
        assertTrue(recipients.contains("AB+"));
        assertFalse(recipients.contains("O-"));
        assertFalse(recipients.contains("A-"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"})
    @DisplayName("All valid blood types should pass validation")
    void testValidBloodTypes(String bloodType) {
        assertTrue(BloodTypeCompatibility.isValidBloodType(bloodType));
        assertDoesNotThrow(() -> BloodTypeCompatibility.validateBloodType(bloodType));
    }

    @ParameterizedTest
    @ValueSource(strings = {"X+", "C-", "AB", "O", "", "invalid", "A++", "OO-"})
    @DisplayName("Invalid blood types should fail validation")
    void testInvalidBloodTypes(String bloodType) {
        assertFalse(BloodTypeCompatibility.isValidBloodType(bloodType));
        assertThrows(IllegalArgumentException.class, 
            () -> BloodTypeCompatibility.validateBloodType(bloodType));
    }

    @Test
    @DisplayName("Null blood type should fail validation")
    void testNullBloodType() {
        assertFalse(BloodTypeCompatibility.isValidBloodType(null));
        assertThrows(IllegalArgumentException.class, 
            () -> BloodTypeCompatibility.validateBloodType(null));
    }

    @Test
    @DisplayName("Extract ABO group correctly")
    void testGetABOGroup() {
        assertEquals("A", BloodTypeCompatibility.getABOGroup("A+"));
        assertEquals("A", BloodTypeCompatibility.getABOGroup("A-"));
        assertEquals("B", BloodTypeCompatibility.getABOGroup("B+"));
        assertEquals("AB", BloodTypeCompatibility.getABOGroup("AB+"));
        assertEquals("O", BloodTypeCompatibility.getABOGroup("O-"));
    }

    @Test
    @DisplayName("Extract Rh factor correctly")
    void testGetRhFactor() {
        assertEquals("+", BloodTypeCompatibility.getRhFactor("A+"));
        assertEquals("-", BloodTypeCompatibility.getRhFactor("A-"));
        assertEquals("+", BloodTypeCompatibility.getRhFactor("O+"));
        assertEquals("-", BloodTypeCompatibility.getRhFactor("AB-"));
    }

    @Test
    @DisplayName("Check Rh positive status")
    void testIsRhPositive() {
        assertTrue(BloodTypeCompatibility.isRhPositive("A+"));
        assertTrue(BloodTypeCompatibility.isRhPositive("O+"));
        assertFalse(BloodTypeCompatibility.isRhPositive("A-"));
        assertFalse(BloodTypeCompatibility.isRhPositive("O-"));
    }

    @Test
    @DisplayName("Universal donor check")
    void testIsUniversalDonor() {
        assertTrue(BloodTypeCompatibility.isUniversalDonor("O-"));
        assertFalse(BloodTypeCompatibility.isUniversalDonor("O+"));
        assertFalse(BloodTypeCompatibility.isUniversalDonor("AB-"));
    }

    @Test
    @DisplayName("Universal recipient check")
    void testIsUniversalRecipient() {
        assertTrue(BloodTypeCompatibility.isUniversalRecipient("AB+"));
        assertFalse(BloodTypeCompatibility.isUniversalRecipient("AB-"));
        assertFalse(BloodTypeCompatibility.isUniversalRecipient("O+"));
    }

    @Test
    @DisplayName("Get all blood types returns 8 types")
    void testGetAllBloodTypes() {
        assertEquals(8, BloodTypeCompatibility.getAllBloodTypes().size());
    }

    @Test
    @DisplayName("Compatibility summary contains expected information")
    void testCompatibilitySummary() {
        String summary = BloodTypeCompatibility.getCompatibilitySummary("O-");
        
        assertTrue(summary.contains("Blood Type: O-"));
        assertTrue(summary.contains("ABO Group: O"));
        assertTrue(summary.contains("Rh Factor: -"));
        assertTrue(summary.contains("Universal RBC Donor"));
    }

    @Test
    @DisplayName("Case insensitive blood type handling")
    void testCaseInsensitive() {
        assertTrue(BloodTypeCompatibility.canDonateRBCTo("o-", "A+"));
        assertTrue(BloodTypeCompatibility.canDonateRBCTo("O-", "a+"));
        assertTrue(BloodTypeCompatibility.isValidBloodType("ab+"));
        assertTrue(BloodTypeCompatibility.isValidBloodType("Ab-"));
    }
}
