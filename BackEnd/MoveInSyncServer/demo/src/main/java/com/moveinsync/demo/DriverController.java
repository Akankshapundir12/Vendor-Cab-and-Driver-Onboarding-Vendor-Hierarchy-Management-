package com.moveinsync.demo;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.*;
import java.io.*;

@RestController
@RequestMapping("/api/driver")
public class DriverController {

    private static final String CSV_FILE = "databases/driver.csv";
    private static final List<Driver> drivers = new ArrayList<>();
    static {
        loadDriversFromCsv();
    }

    @GetMapping("/getdrivers")
    public ResponseEntity<Object> getDrivers() {
        System.out.println("[GET /api/driver/getdrivers] Request received");
        loadDriversFromCsv();
        System.out.println("[GET /api/driver/getdrivers] Response: " + drivers);
        return ResponseEntity.ok(drivers);
    }

    @PostMapping("/updatedriver")
    public ResponseEntity<Object> updateDriver(@RequestBody Driver driver) {
        System.out.println("[POST /api/driver/updatedriver] Request received: " + driver);
        // Only check for these fields from client
        if (driver == null || driver.getDriver() == null || driver.getLicense() == null || driver.getVehicle() == null || driver.getDriverNumber() == null) {
            System.out.println("[POST /api/driver/updatedriver] Error: driver, license, vehicle, and driverNumber are required");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("error", "driver, license, vehicle, and driverNumber are required"));
        }
        // Check for duplicate license or vehicle
        for (Driver d : drivers) {
            if (d.getLicense() != null && d.getLicense().equals(driver.getLicense())) {
                System.out.println("[POST /api/driver/updatedriver] Error: licence already in record");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("error", "licence already in record"));
            }
            if (d.getVehicle() != null && d.getVehicle().equals(driver.getVehicle())) {
                System.out.println("[POST /api/driver/updatedriver] Error: vehicle already registered");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("error", "vehicle already registered"));
            }
        }
        int docCount = 0;
        if (driver.getDocuments() != null) {
            // Handle case where document is like "2/3" or "3/3"
            if (driver.getDocuments().size() == 1 && driver.getDocuments().get(0).matches("\\d/3")) {
                String[] parts = driver.getDocuments().get(0).split("/");
                try {
                    docCount = Integer.parseInt(parts[0]);
                } catch (Exception e) {
                    docCount = 0;
                }
                System.out.println("[POST /api/driver/updatedriver] Parsed docCount from string: " + driver.getDocuments().get(0) + " => " + docCount);
            } else {
                docCount = driver.getDocuments().size();
                System.out.println("[POST /api/driver/updatedriver] docCount from array: " + docCount);
            }
        }
        if (docCount > 3) {
            System.out.println("[POST /api/driver/updatedriver] Error: documents can be at most 3");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("error", "documents can be at most 3"));
        }
        // Assign default values for all other fields
        if (driver.getVehicleId() == null) driver.setVehicleId("");
        if (driver.getVehicleNumber() == null) driver.setVehicleNumber("");
        if (docCount == 3) {
            driver.setOnboarding("Completed");
            driver.setStatus("Active");
        } else if (docCount == 2) {
            driver.setOnboarding("One step left");
            driver.setStatus("inactive");
        } else {
            driver.setOnboarding("pending");
            driver.setStatus("inactive");
        }
        drivers.add(driver);
        saveDriversToCsv();
        System.out.println("[POST /api/driver/updatedriver] Driver added: " + driver);
        return ResponseEntity.ok(Collections.singletonMap("message", "Driver added successfully"));
    }

    private static void loadDriversFromCsv() {
        drivers.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILE))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) { firstLine = false; continue; }
                String[] parts = line.split(",", -1);
                if (parts.length >= 9) {
                    Driver d = new Driver();
                    d.setVehicleId(parts[0].trim());
                    d.setDriverNumber(parts[1].trim());
                    d.setDriver(parts[2].trim());
                    d.setLicense(parts[3].trim());
                    d.setVehicle(parts[4].trim());
                    d.setVehicleNumber(parts[5].trim());
                    d.setStatus(parts[6].trim());
                    d.setOnboarding(parts[7].trim());
                    if (!parts[8].trim().isEmpty()) {
                        d.setDocuments(Arrays.asList(parts[8].trim().split(";")));
                    } else {
                        d.setDocuments(new ArrayList<>());
                    }
                    drivers.add(d);
                }
            }
        } catch (IOException e) {
            // Ignore if file doesn't exist or is empty
        }
    }

    private static void saveDriversToCsv() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(CSV_FILE))) {
            pw.println("vehicleId,driverNumber,driver,license,vehicle,vehicleNumber,status,onboarding,documents");
            for (Driver d : drivers) {
                String docs = d.getDocuments() != null ? String.join(";", d.getDocuments()) : "";
                pw.println(String.join(",",
                    safe(d.getVehicleId()),
                    safe(d.getDriverNumber()),
                    safe(d.getDriver()),
                    safe(d.getLicense()),
                    safe(d.getVehicle()),
                    safe(d.getVehicleNumber()),
                    safe(d.getStatus()),
                    safe(d.getOnboarding()),
                    docs
                ));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String safe(String s) {
        return s == null ? "" : s.replace(",", " ").replace("\n", " ");
    }

    public static class Driver {
        private String vehicleId;
        private String vehicleNumber;
        private String driver;
        private String license;
        private String vehicle;
        private String driverNumber;
        private String status;
        private String onboarding;
        private List<String> documents;

        public String getVehicleId() { return vehicleId; }
        public void setVehicleId(String vehicleId) { this.vehicleId = vehicleId; }
        public String getVehicleNumber() { return vehicleNumber; }
        public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }
        public String getDriver() { return driver; }
        public void setDriver(String driver) { this.driver = driver; }
        public String getLicense() { return license; }
        public void setLicense(String license) { this.license = license; }
        public String getVehicle() { return vehicle; }
        public void setVehicle(String vehicle) { this.vehicle = vehicle; }
        public String getDriverNumber() { return driverNumber; }
        public void setDriverNumber(String driverNumber) { this.driverNumber = driverNumber; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getOnboarding() { return onboarding; }
        public void setOnboarding(String onboarding) { this.onboarding = onboarding; }
        public List<String> getDocuments() { return documents; }
        public void setDocuments(Object documents) {
            if (documents instanceof List) {
                this.documents = (List<String>) documents;
            } else if (documents instanceof String) {
                String docStr = (String) documents;
                if (!docStr.trim().isEmpty()) {
                    this.documents = Arrays.asList(docStr.split(";"));
                } else {
                    this.documents = new ArrayList<>();
                }
            } else {
                this.documents = new ArrayList<>();
            }
        }

        @Override
        public String toString() {
            return "Driver{" +
                    "vehicleId='" + vehicleId + '\'' +
                    ", vehicleNumber='" + vehicleNumber + '\'' +
                    ", driver='" + driver + '\'' +
                    ", license='" + license + '\'' +
                    ", vehicle='" + vehicle + '\'' +
                    ", driverNumber='" + driverNumber + '\'' +
                    ", status='" + status + '\'' +
                    ", onboarding='" + onboarding + '\'' +
                    ", documents=" + documents +
                    '}';
        }
    }
}
