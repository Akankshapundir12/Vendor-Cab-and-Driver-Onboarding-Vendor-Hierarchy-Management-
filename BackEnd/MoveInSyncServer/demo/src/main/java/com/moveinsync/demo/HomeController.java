package com.moveinsync.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
public class HomeController {
    @Autowired
    private VendorService vendorService;

    @GetMapping("/")
    public ResponseEntity<String> home() {
        return ResponseEntity.ok("You have got response from Moveinsync Server ft. Akanksha Pundir.");
    }

    @GetMapping("/gethomepagedata")
    public ResponseEntity<HomePageData> getHomePageData() {
        System.out.println("[HomeController] /gethomepagedata called");
        HomePageData data = new HomePageData();
        data.setTotalVehicle(vendorService.getTotalActiveCount());
        data.setActiveDrivers(vendorService.getTotalDriversCount());
        data.setPendingApproval(vendorService.getTotalPendingRequestCount());
        System.out.println("[HomeController] Response: {" +
            "\"totalVehicle\":" + data.getTotalVehicle() + ", " +
            "\"activeDrivers\":" + data.getActiveDrivers() + ", " +
            "\"pendingApproval\":" + data.getPendingApproval() +
        "}");
        return ResponseEntity.ok(data);
    }

    public static class HomePageData {
        private int totalVehicle;
        private int activeDrivers;
        private int pendingApproval;

        public int getTotalVehicle() { return totalVehicle; }
        public void setTotalVehicle(int totalVehicle) { this.totalVehicle = totalVehicle; }
        public int getActiveDrivers() { return activeDrivers; }
        public void setActiveDrivers(int activeDrivers) { this.activeDrivers = activeDrivers; }
        public int getPendingApproval() { return pendingApproval; }
        public void setPendingApproval(int pendingApproval) { this.pendingApproval = pendingApproval; }
    }
}
