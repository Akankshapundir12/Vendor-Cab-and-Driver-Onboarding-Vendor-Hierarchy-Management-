package com.moveinsync.demo;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.*;

@RestController
@RequestMapping("/api")
public class DashboardController {

    @Autowired
    private VendorService vendorService;

    @GetMapping("/user")
    public ResponseEntity<Object> getUser(@RequestParam String email) {
        System.out.println("[GET /api/user] email param: " + email);
        // Simulate lookup from users.csv
        CsvUserDatabase.UserData user = CsvUserDatabase.getUser(email);
        if (user == null) {
            System.out.println("[GET /api/user] User not found for email: " + email);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("error", "User not found"));
        }
        Map<String, String> resp = new HashMap<>();
        resp.put("name", user.getName());
        resp.put("email", user.getUsername());
        System.out.println("[GET /api/user] Response: " + resp);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Object> getDashboard(@RequestParam String email) {
        System.out.println("[GET /api/dashboard] email param: " + email);
        Map<String, Object> resp = new HashMap<>();
        resp.put("totalDrivers", vendorService.getTotalDriversCount());
        resp.put("pendingApprovals", vendorService.getTotalPendingRequestCount());
        resp.put("activeVehicles", vendorService.getTotalActiveCount());
        System.out.println("[GET /api/dashboard] Response: " + resp);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/performance")
    public ResponseEntity<Object> getPerformance(@RequestParam String email) {
        System.out.println("[GET /api/performance] email param: " + email);
        // Dummy data, replace with real logic as needed
        Map<String, Object> resp = new HashMap<>();
        resp.put("labels", Arrays.asList("North", "South", "East", "West"));
        resp.put("values", Arrays.asList(2.5, 3, 1.8, 2.2));
        System.out.println("[GET /api/performance] Response: " + resp);
        return ResponseEntity.ok(resp);
    }
}
