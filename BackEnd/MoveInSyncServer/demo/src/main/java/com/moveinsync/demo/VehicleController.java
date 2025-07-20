package com.moveinsync.demo;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.*;
import java.io.*;

@RestController
@RequestMapping("/api/vehicle")
public class VehicleController {
    private static final String CSV_FILE = "databases/vehicle.csv";
    private static final List<Vehicle> vehicles = new ArrayList<>();
    static {
        loadVehiclesFromCsv();
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllVehicles() {
        loadVehiclesFromCsv();
        return ResponseEntity.ok(vehicles);
    }

    @PostMapping("/add")
    public ResponseEntity<Object> addVehicle(@RequestBody Vehicle vehicle) {
        if (vehicle == null || vehicle.getRegistration() == null || vehicle.getModel() == null || vehicle.getType() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("error", "registration, model, and type are required"));
        }
        // Check for duplicate registration
        for (Vehicle v : vehicles) {
            if (v.getRegistration() != null && v.getRegistration().equalsIgnoreCase(vehicle.getRegistration())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("error", "Vehicle with this registration already exists"));
            }
        }
        vehicles.add(vehicle);
        saveVehiclesToCsv();
        return ResponseEntity.ok(Collections.singletonMap("message", "Vehicle added successfully"));
    }

    private static void loadVehiclesFromCsv() {
        vehicles.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILE))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) { firstLine = false; continue; }
                String[] parts = line.split(",", -1);
                if (parts.length >= 8) {
                    Vehicle v = new Vehicle();
                    v.setRegistration(parts[0].trim());
                    v.setModel(parts[1].trim());
                    v.setType(parts[2].trim());
                    v.setCapacity(parts[3].trim());
                    v.setFuel(parts[4].trim());
                    v.setCompliance(parts[5].trim());
                    v.setStatus(parts[6].trim());
                    v.setAction(parts[7].trim());
                    vehicles.add(v);
                }
            }
        } catch (IOException e) {
            // Ignore if file doesn't exist or is empty
        }
    }

    private static void saveVehiclesToCsv() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(CSV_FILE))) {
            pw.println("registration,model,type,capacity,fuel,compliance,status,action");
            for (Vehicle v : vehicles) {
                pw.println(String.join(",",
                    safe(v.getRegistration()),
                    safe(v.getModel()),
                    safe(v.getType()),
                    safe(v.getCapacity()),
                    safe(v.getFuel()),
                    safe(v.getCompliance()),
                    safe(v.getStatus()),
                    safe(v.getAction())
                ));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String safe(String s) {
        return s == null ? "" : s.replace(",", " ").replace("\n", " ");
    }

    public static class Vehicle {
        private String registration;
        private String model;
        private String type;
        private String capacity;
        private String fuel;
        private String compliance;
        private String status;
        private String action;

        public String getRegistration() { return registration; }
        public void setRegistration(String registration) { this.registration = registration; }
        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getCapacity() { return capacity; }
        public void setCapacity(String capacity) { this.capacity = capacity; }
        public String getFuel() { return fuel; }
        public void setFuel(String fuel) { this.fuel = fuel; }
        public String getCompliance() { return compliance; }
        public void setCompliance(String compliance) { this.compliance = compliance; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getAction() { return action; }
        public void setAction(String action) { this.action = action; }
    }
}
