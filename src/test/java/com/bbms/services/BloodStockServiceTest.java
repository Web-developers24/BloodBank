package com.bbms.services;

import com.bbms.dao.BloodStockDao;
import com.bbms.models.BloodStock;
import com.bbms.models.BloodStock.ComponentType;
import com.bbms.models.BloodStock.StockStatus;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BloodStockService Tests")
class BloodStockServiceTest {
    
    @Mock
    private BloodStockDao stockDao;
    
    @InjectMocks
    private BloodStockService stockService;
    
    private BloodStock createBloodStock(String bloodGroup, int quantity) {
        BloodStock stock = new BloodStock();
        stock.setBloodGroup(bloodGroup);
        stock.setQuantity(quantity);
        stock.setComponentType(ComponentType.WHOLE_BLOOD);
        stock.setStatus(StockStatus.AVAILABLE);
        stock.setExpiryDate(LocalDate.now().plusDays(30));
        return stock;
    }
    
    @Nested
    @DisplayName("Availability Tests")
    class AvailabilityTests {
        
        @Test
        @DisplayName("Should return true when stock is sufficient")
        void shouldReturnTrueWhenStockIsSufficient() {
            // Given
            BloodStock stock = createBloodStock("O+", 10);
            
            when(stockDao.findAvailableByBloodGroup("O+"))
                .thenReturn(Collections.singletonList(stock));
            
            // When
            boolean available = stockService.checkAvailability("O+", ComponentType.WHOLE_BLOOD, 5);
            
            // Then
            assertTrue(available);
        }
        
        @Test
        @DisplayName("Should return false when stock is insufficient")
        void shouldReturnFalseWhenStockIsInsufficient() {
            // Given
            BloodStock stock = createBloodStock("O+", 3);
            
            when(stockDao.findAvailableByBloodGroup("O+"))
                .thenReturn(Collections.singletonList(stock));
            
            // When
            boolean available = stockService.checkAvailability("O+", ComponentType.WHOLE_BLOOD, 5);
            
            // Then
            assertFalse(available);
        }
        
        @Test
        @DisplayName("Should return false when no stock exists")
        void shouldReturnFalseWhenNoStockExists() {
            // Given
            when(stockDao.findAvailableByBloodGroup("AB-"))
                .thenReturn(Collections.emptyList());
            
            // When
            boolean available = stockService.checkAvailability("AB-", ComponentType.WHOLE_BLOOD, 1);
            
            // Then
            assertFalse(available);
        }
    }
    
    @Nested
    @DisplayName("Expiry Alert Tests")
    class ExpiryAlertTests {
        
        @Test
        @DisplayName("Should return expiring stock")
        void shouldReturnExpiringStock() {
            // Given
            BloodStock expiringStock = createBloodStock("A+", 5);
            expiringStock.setExpiryDate(LocalDate.now().plusDays(3));
            
            when(stockDao.findExpiringWithinDays(7))
                .thenReturn(Collections.singletonList(expiringStock));
            
            // When
            List<BloodStock> expiring = stockService.getExpiringStock(7);
            
            // Then
            assertEquals(1, expiring.size());
            verify(stockDao).findExpiringWithinDays(7);
        }
        
        @Test
        @DisplayName("Should return empty list when no expiring stock")
        void shouldReturnEmptyListWhenNoExpiringStock() {
            // Given
            when(stockDao.findExpiringWithinDays(7))
                .thenReturn(Collections.emptyList());
            
            // When
            List<BloodStock> expiring = stockService.getExpiringStock(7);
            
            // Then
            assertTrue(expiring.isEmpty());
        }
    }
    
    @Nested
    @DisplayName("Low Stock Alert Tests")
    class LowStockAlertTests {
        
        @Test
        @DisplayName("Should detect low stock levels")
        void shouldDetectLowStockLevels() {
            // Given
            BloodStock lowStock = createBloodStock("B-", 3);
            
            when(stockDao.findLowStock(10))
                .thenReturn(Collections.singletonList(lowStock));
            
            // When
            List<BloodStock> low = stockService.getLowStockAlerts();
            
            // Then
            assertEquals(1, low.size());
            assertEquals("B-", low.get(0).getBloodGroup());
        }
    }
    
    @Nested
    @DisplayName("Compatibility Tests")
    class CompatibilityTests {
        
        @Test
        @DisplayName("O- should be compatible with all blood types")
        void oNegativeShouldBeCompatibleWithAll() {
            // Given
            String universalDonor = "O-";
            String[] allTypes = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
            
            // Then - O- can donate to all
            for (String recipientType : allTypes) {
                assertTrue(BloodStockService.canDonate(universalDonor, recipientType),
                    "O- should be able to donate to " + recipientType);
            }
        }
        
        @Test
        @DisplayName("AB+ should receive from all blood types")
        void abPositiveShouldReceiveFromAll() {
            // Given
            String universalRecipient = "AB+";
            String[] allTypes = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
            
            // Then - AB+ can receive from all
            for (String donorType : allTypes) {
                assertTrue(BloodStockService.canDonate(donorType, universalRecipient),
                    universalRecipient + " should be able to receive from " + donorType);
            }
        }
        
        @Test
        @DisplayName("A+ cannot donate to B+")
        void aPositiveCannotDonateToBPositive() {
            // When
            boolean canDonate = BloodStockService.canDonate("A+", "B+");
            
            // Then
            assertFalse(canDonate);
        }
        
        @Test
        @DisplayName("Same blood type should always be compatible")
        void sameBloodTypeShouldBeCompatible() {
            // Given
            String[] allTypes = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
            
            // Then
            for (String type : allTypes) {
                assertTrue(BloodStockService.canDonate(type, type),
                    type + " should be compatible with itself");
            }
        }
    }
}
