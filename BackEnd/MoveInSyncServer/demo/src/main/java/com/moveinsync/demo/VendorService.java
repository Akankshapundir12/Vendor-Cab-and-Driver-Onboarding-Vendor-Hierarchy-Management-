package com.moveinsync.demo;

import java.util.*;

import org.springframework.stereotype.Service;

@Service
public class VendorService {
    private final Map<String, SuperVendor> superVendors = new HashMap<>();
    private final Map<String, RegionalVendor> regionalVendors = new HashMap<>();
    private final Map<String, CityVendor> cityVendors = new HashMap<>();
    private final Map<String, LocalVendor> localVendors = new HashMap<>();

    private int totalActiveCount = 0;
    private int totalDriversCount = 0;
    private int totalPendingRequestCount = 0;

    public Map<String, SuperVendor> getSuperVendors() { return superVendors; }
    public Map<String, RegionalVendor> getRegionalVendors() { return regionalVendors; }
    public Map<String, CityVendor> getCityVendors() { return cityVendors; }
    public Map<String, LocalVendor> getLocalVendors() { return localVendors; }
    public int getTotalActiveCount() { return totalActiveCount; }
    public int getTotalDriversCount() { return totalDriversCount; }
    public int getTotalPendingRequestCount() { return totalPendingRequestCount; }

    public VendorService() {
        loadAllVendors();
    }

    public void loadAllVendors() {
        VendorCsvLoader.loadSuperVendors(superVendors, "databases/super_vendors.csv");
        VendorCsvLoader.loadRegionalVendors(regionalVendors, "databases/regional_vendors.csv");
        VendorCsvLoader.loadCityVendors(cityVendors, "databases/city_vendors.csv");
        VendorCsvLoader.loadLocalVendors(localVendors, "databases/local_vendors.csv");
        // Count logic
        totalActiveCount = 0;
        totalDriversCount = 0;
        totalPendingRequestCount = 0;
        // Example: count all vendors with status active
        for (Vendor v : superVendors.values()) if ("active".equalsIgnoreCase(v.getStatus())) totalActiveCount++;
        for (Vendor v : regionalVendors.values()) if ("active".equalsIgnoreCase(v.getStatus())) totalActiveCount++;
        for (Vendor v : cityVendors.values()) if ("active".equalsIgnoreCase(v.getStatus())) totalActiveCount++;
        for (Vendor v : localVendors.values()) if ("active".equalsIgnoreCase(v.getStatus())) totalActiveCount++;
        // Example: treat all local vendors as drivers
        totalDriversCount = localVendors.size();
        // Example: pending = vendors with status not active
        for (Vendor v : superVendors.values()) if (!"active".equalsIgnoreCase(v.getStatus())) totalPendingRequestCount++;
        for (Vendor v : regionalVendors.values()) if (!"active".equalsIgnoreCase(v.getStatus())) totalPendingRequestCount++;
        for (Vendor v : cityVendors.values()) if (!"active".equalsIgnoreCase(v.getStatus())) totalPendingRequestCount++;
        for (Vendor v : localVendors.values()) if (!"active".equalsIgnoreCase(v.getStatus())) totalPendingRequestCount++;
        System.out.println("[VendorService] Total Active Vendors: " + totalActiveCount);
        System.out.println("[VendorService] Total Drivers: " + totalDriversCount);
        System.out.println("[VendorService] Total Pending Requests: " + totalPendingRequestCount);
    }

    public Vendor getVendorHierarchy(String username, String vendorType) {
        Vendor vendor = null;
        switch (vendorType.toLowerCase()) {
            case "super":
                vendor = superVendors.get(username);
                break;
            case "regional":
                vendor = regionalVendors.get(username);
                break;
            case "city":
                vendor = cityVendors.get(username);
                break;
            case "local":
                vendor = localVendors.get(username);
                break;
        }
        if (vendor != null) {
            buildHierarchy(vendor);
        }
        return vendor;
    }

    private void buildHierarchy(Vendor vendor) {
        if (vendor.getChildrenUsernames() == null) return;
        List<Vendor> children = new ArrayList<>();
        for (String childUsername : vendor.getChildrenUsernames()) {
            Vendor child = null;
            switch (vendor.getLevel()) {
                case "super":
                    child = regionalVendors.get(childUsername);
                    break;
                case "regional":
                    child = cityVendors.get(childUsername);
                    break;
                case "city":
                    child = localVendors.get(childUsername);
                    break;
            }
            if (child != null) {
                buildHierarchy(child);
                children.add(child);
            }
        }
        vendor.setChildren(children);
    }

    public static class Vendor {
        private String vendorId;
        private String name;
        private String level;
        private String status;
        private List<String> childrenUsernames;
        private List<Vendor> children;
        private List<String> permissions;
        private Map<String, Object> accessControl;
        private String phone;

        // Getters and setters
        public String getVendorId() { return vendorId; }
        public void setVendorId(String vendorId) { this.vendorId = vendorId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getLevel() { return level; }
        public void setLevel(String level) { this.level = level; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public List<String> getChildrenUsernames() { return childrenUsernames; }
        public void setChildrenUsernames(List<String> childrenUsernames) { this.childrenUsernames = childrenUsernames; }
        public List<Vendor> getChildren() { return children; }
        public void setChildren(List<Vendor> children) { this.children = children; }
        public List<String> getPermissions() { return permissions; }
        public void setPermissions(List<String> permissions) { this.permissions = permissions; }
        public Map<String, Object> getAccessControl() { return accessControl; }
        public void setAccessControl(Map<String, Object> accessControl) { this.accessControl = accessControl; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }

        // OOP: Access control methods
        public boolean canAddSubVendor() { return false; }
        public boolean canAssignLocalVendor() { return false; }
        public boolean canEditVehicles() { return false; }
        public boolean canEditDrivers() { return false; }
    }

    public static class SuperVendor extends Vendor {
        @Override
        public boolean canAddSubVendor() { return true; }
        // SuperVendor can do everything
        @Override
        public boolean canAssignLocalVendor() { return true; }
        @Override
        public boolean canEditVehicles() { return true; }
        @Override
        public boolean canEditDrivers() { return true; }
    }

    public static class RegionalVendor extends Vendor {
        @Override
        public boolean canAddSubVendor() { return false; }
        @Override
        public boolean canAssignLocalVendor() { return true; }
        @Override
        public boolean canEditVehicles() { return true; }
        @Override
        public boolean canEditDrivers() { return true; }
    }

    public static class CityVendor extends Vendor {
        @Override
        public boolean canAddSubVendor() { return false; }
        @Override
        public boolean canAssignLocalVendor() { return true; }
        @Override
        public boolean canEditVehicles() { return true; }
        @Override
        public boolean canEditDrivers() { return true; }
    }

    public static class LocalVendor extends Vendor {
        @Override
        public boolean canAddSubVendor() { return false; }
        @Override
        public boolean canAssignLocalVendor() { return false; }
        @Override
        public boolean canEditVehicles() { return false; }
        @Override
        public boolean canEditDrivers() { return false; }
    }
}
