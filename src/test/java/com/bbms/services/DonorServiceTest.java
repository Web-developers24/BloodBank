package com.bbms.services;

import com.bbms.dao.DonorDao;
import com.bbms.dao.DonationDao;
import com.bbms.models.Donor;
import com.bbms.models.Donor.Gender;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DonorService Tests")
class DonorServiceTest {
    
    @Mock
    private DonorDao donorDao;
    
    @Mock
    private DonationDao donationDao;
    
    @InjectMocks
    private DonorService donorService;
    
    private Donor createValidDonor() {
        Donor donor = new Donor();
        donor.setId(1L);
        donor.setName("John Doe");
        donor.setBloodGroup("O+");
        donor.setDateOfBirth(LocalDate.now().minusYears(30));
        donor.setGender(Gender.MALE);
        donor.setWeightKg(75.0);
        donor.setPhone("1234567890");
        donor.setActive(true);
        return donor;
    }
    
    @Nested
    @DisplayName("Eligibility Tests")
    class EligibilityTests {
        
        @Test
        @DisplayName("Should return eligible donor with no recent donation")
        void shouldReturnEligibleDonorWithNoRecentDonation() {
            // Given
            Donor donor = createValidDonor();
            donor.setLastDonationDate(LocalDate.now().minusDays(100)); // More than 90 days
            
            // When
            boolean eligible = donor.isEligible();
            
            // Then
            assertTrue(eligible);
        }
        
        @Test
        @DisplayName("Should return ineligible donor with recent donation")
        void shouldReturnIneligibleDonorWithRecentDonation() {
            // Given
            Donor donor = createValidDonor();
            donor.setLastDonationDate(LocalDate.now().minusDays(30)); // Less than 90 days
            
            // When
            boolean eligible = donor.isEligible();
            
            // Then
            assertFalse(eligible);
        }
        
        @Test
        @DisplayName("Should return ineligible donor under minimum age")
        void shouldReturnIneligibleDonorUnderMinimumAge() {
            // Given
            Donor donor = createValidDonor();
            donor.setDateOfBirth(LocalDate.now().minusYears(16)); // Under 18
            
            // When
            boolean eligible = donor.isEligible();
            
            // Then
            assertFalse(eligible);
        }
        
        @Test
        @DisplayName("Should return ineligible donor over maximum age")
        void shouldReturnIneligibleDonorOverMaximumAge() {
            // Given
            Donor donor = createValidDonor();
            donor.setDateOfBirth(LocalDate.now().minusYears(70)); // Over 65
            
            // When
            boolean eligible = donor.isEligible();
            
            // Then
            assertFalse(eligible);
        }
        
        @Test
        @DisplayName("Should return ineligible donor under minimum weight")
        void shouldReturnIneligibleDonorUnderMinimumWeight() {
            // Given
            Donor donor = createValidDonor();
            donor.setWeightKg(45.0); // Under 50 kg
            
            // When
            boolean eligible = donor.isEligible();
            
            // Then
            assertFalse(eligible);
        }
        
        @Test
        @DisplayName("Should return ineligible inactive donor")
        void shouldReturnIneligibleInactiveDonor() {
            // Given
            Donor donor = createValidDonor();
            donor.setActive(false);
            
            // When
            boolean eligible = donor.isEligible();
            
            // Then
            assertFalse(eligible);
        }
    }
    
    @Nested
    @DisplayName("Donor Search Tests")
    class DonorSearchTests {
        
        @Test
        @DisplayName("Should find donors by blood group")
        void shouldFindDonorsByBloodGroup() {
            // Given
            Donor donor1 = createValidDonor();
            Donor donor2 = createValidDonor();
            donor2.setId(2L);
            donor2.setName("Jane Doe");
            
            when(donorDao.findByBloodGroup("O+")).thenReturn(Arrays.asList(donor1, donor2));
            
            // When
            List<Donor> result = donorDao.findByBloodGroup("O+");
            
            // Then
            assertEquals(2, result.size());
            verify(donorDao).findByBloodGroup("O+");
        }
    }
    
    @Nested
    @DisplayName("Days Until Eligible Tests")
    class DaysUntilEligibleTests {
        
        @Test
        @DisplayName("Should return 0 when already eligible")
        void shouldReturnZeroWhenAlreadyEligible() {
            // Given
            Donor donor = createValidDonor();
            donor.setLastDonationDate(LocalDate.now().minusDays(100));
            
            // When
            int days = donor.getDaysUntilEligible();
            
            // Then
            assertEquals(0, days);
        }
        
        @Test
        @DisplayName("Should return correct days when not yet eligible")
        void shouldReturnCorrectDaysWhenNotYetEligible() {
            // Given
            Donor donor = createValidDonor();
            donor.setLastDonationDate(LocalDate.now().minusDays(60)); // 30 days remaining
            
            // When
            int days = donor.getDaysUntilEligible();
            
            // Then
            assertEquals(30, days);
        }
        
        @Test
        @DisplayName("Should return 0 when no previous donation")
        void shouldReturnZeroWhenNoPreviousDonation() {
            // Given
            Donor donor = createValidDonor();
            donor.setLastDonationDate(null);
            
            // When
            int days = donor.getDaysUntilEligible();
            
            // Then
            assertEquals(0, days);
        }
    }
}
