package org.example;

import org.example.dao.*;
import org.example.model.*;

public class Main {
    public static void main(String[] args) {
        DonorDao donorDao = new DonorDao();
        Donor donor = new Donor();
        donor.setFullName("Arjun Kumar");
        donor.setBloodGroup("A+");
        donor.setPhone("8888800000");
        donorDao.saveDonor(donor);

        BloodStockDao stockDao = new BloodStockDao();
        System.out.println("Current Stock:");
        for (BloodStock s : stockDao.getAllStocks()) {
            System.out.println(s.getBloodGroup() + " - " + s.getUnitsAvailable() + " units");
        }
    }
}
