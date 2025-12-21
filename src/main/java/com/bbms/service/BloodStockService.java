package com.bbms.service;

import com.bbms.dao.BloodStockDao;
import com.bbms.model.BloodStock;
import com.bbms.util.BloodCompatibility;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.util.*;

public class BloodStockService {

    private static final Logger logger = LogManager.getLogger(BloodStockService.class);
    private static final int LOW_STOCK_THRESHOLD = 5;
    private static final int EXPIRY_WARNING_DAYS = 7;

    private final BloodStockDao bloodStockDao;

    public BloodStockService() {
        this.bloodStockDao = new BloodStockDao();
    }

    public BloodStock saveStock(BloodStock stock) {
        if (stock.getId() == null) {
            logger.info("Adding blood stock: {} {}", stock.getBloodGroup(), stock.getComponentType());
            return bloodStockDao.save(stock);
        } else {
            logger.info("Updating blood stock ID: {}", stock.getId());
            return bloodStockDao.update(stock);
        }
    }

    public void deleteStock(Long id) {
        bloodStockDao.deleteById(id);
        logger.info("Deleted blood stock ID: {}", id);
    }

    public Optional<BloodStock> findById(Long id) {
        return bloodStockDao.findById(id);
    }

    public List<BloodStock> findAll() {
        return bloodStockDao.findAll();
    }

    public List<BloodStock> findAvailable() {
        return bloodStockDao.findAvailable();
    }

    public List<BloodStock> findByBloodGroup(String bloodGroup) {
        return bloodStockDao.findByBloodGroup(bloodGroup);
    }

    public int getTotalUnits(String bloodGroup) {
        return bloodStockDao.getTotalUnitsByBloodGroup(bloodGroup);
    }

    /**
     * Get stock summary grouped by blood group.
     */
    public Map<String, Integer> getStockSummary() {
        Map<String, Integer> summary = new LinkedHashMap<>();
        for (String bloodGroup : BloodCompatibility.ALL_BLOOD_GROUPS) {
            summary.put(bloodGroup, getTotalUnits(bloodGroup));
        }
        return summary;
    }

    /**
     * Get stocks expiring within specified days.
     */
    public List<BloodStock> getExpiringSoon() {
        return bloodStockDao.findExpiringSoon(EXPIRY_WARNING_DAYS);
    }

    /**
     * Get stocks that are low.
     */
    public List<BloodStock> getLowStock() {
        return bloodStockDao.findLowStock(LOW_STOCK_THRESHOLD);
    }

    /**
     * Get expired stocks.
     */
    public List<BloodStock> getExpiredStock() {
        return bloodStockDao.findExpired();
    }

    /**
     * Mark expired stocks.
     */
    public int markExpiredStocks() {
        List<BloodStock> expired = getExpiredStock();
        int count = 0;
        for (BloodStock stock : expired) {
            stock.setStatus(BloodStock.StockStatus.EXPIRED);
            bloodStockDao.update(stock);
            count++;
        }
        if (count > 0) {
            logger.warn("Marked {} blood stock(s) as expired", count);
        }
        return count;
    }

    /**
     * Add stock from a donation.
     */
    public BloodStock addFromDonation(String bloodGroup, BloodStock.ComponentType componentType, 
                                       int units, int volumeMl, String storageLocation) {
        BloodStock stock = new BloodStock();
        stock.setBloodGroup(bloodGroup);
        stock.setComponentType(componentType);
        stock.setUnitsAvailable(units);
        stock.setUnitVolumeMl(volumeMl);
        stock.setStorageLocation(storageLocation);
        stock.setCollectionDate(LocalDate.now());
        stock.setStatus(BloodStock.StockStatus.AVAILABLE);
        
        // Set expiry based on component type
        stock.setExpiryDate(calculateExpiryDate(componentType));
        
        return bloodStockDao.save(stock);
    }

    /**
     * Deduct stock for a transfusion.
     */
    public boolean deductStock(Long stockId, int units) {
        Optional<BloodStock> stockOpt = bloodStockDao.findById(stockId);
        if (stockOpt.isEmpty()) {
            return false;
        }

        BloodStock stock = stockOpt.get();
        if (stock.getUnitsAvailable() < units) {
            return false;
        }

        stock.setUnitsAvailable(stock.getUnitsAvailable() - units);
        bloodStockDao.update(stock);
        logger.info("Deducted {} units from stock ID: {}", units, stockId);
        return true;
    }

    /**
     * Find available stock for a recipient blood group.
     */
    public List<BloodStock> findCompatibleStock(String recipientBloodGroup, BloodStock.ComponentType componentType) {
        Set<String> compatibleGroups = BloodCompatibility.getCompatibleDonorGroups(recipientBloodGroup);
        
        return findAvailable().stream()
                .filter(s -> compatibleGroups.contains(s.getBloodGroup()))
                .filter(s -> componentType == null || s.getComponentType() == componentType)
                .filter(s -> !s.isExpired())
                .sorted(Comparator.comparing(BloodStock::getExpiryDate)) // Use oldest first
                .toList();
    }

    /**
     * Calculate expiry date based on component type.
     */
    private LocalDate calculateExpiryDate(BloodStock.ComponentType componentType) {
        return switch (componentType) {
            case WHOLE_BLOOD -> LocalDate.now().plusDays(42);
            case RBC -> LocalDate.now().plusDays(42);
            case PLASMA -> LocalDate.now().plusDays(365); // Frozen plasma lasts 1 year
            case PLATELETS -> LocalDate.now().plusDays(5);
            case WBC -> LocalDate.now().plusDays(1);
        };
    }

    /**
     * Get alerts for stock issues.
     */
    public List<StockAlert> getAlerts() {
        List<StockAlert> alerts = new ArrayList<>();

        // Low stock alerts
        for (BloodStock stock : getLowStock()) {
            alerts.add(new StockAlert(
                    StockAlert.AlertType.LOW_STOCK,
                    String.format("Low stock: %s %s - only %d units left",
                            stock.getBloodGroup(), stock.getComponentType(), stock.getUnitsAvailable()),
                    stock
            ));
        }

        // Expiring soon alerts
        for (BloodStock stock : getExpiringSoon()) {
            alerts.add(new StockAlert(
                    StockAlert.AlertType.EXPIRING_SOON,
                    String.format("Expiring soon: %s %s expires on %s",
                            stock.getBloodGroup(), stock.getComponentType(), stock.getExpiryDate()),
                    stock
            ));
        }

        // Expired alerts
        for (BloodStock stock : getExpiredStock()) {
            alerts.add(new StockAlert(
                    StockAlert.AlertType.EXPIRED,
                    String.format("Expired: %s %s expired on %s",
                            stock.getBloodGroup(), stock.getComponentType(), stock.getExpiryDate()),
                    stock
            ));
        }

        return alerts;
    }

    public record StockAlert(AlertType type, String message, BloodStock stock) {
        public enum AlertType {
            LOW_STOCK, EXPIRING_SOON, EXPIRED
        }
    }
}
