package com.moveinsync.demo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class VendorCsvLoader {
    public static void loadSuperVendors(Map<String, VendorService.SuperVendor> map, String csvPath) {
        loadVendors(map, csvPath, VendorService.SuperVendor.class);
    }

    public static void loadRegionalVendors(Map<String, VendorService.RegionalVendor> map, String csvPath) {
        loadVendors(map, csvPath, VendorService.RegionalVendor.class);
    }

    public static void loadCityVendors(Map<String, VendorService.CityVendor> map, String csvPath) {
        loadVendors(map, csvPath, VendorService.CityVendor.class);
    }

    public static void loadLocalVendors(Map<String, VendorService.LocalVendor> map, String csvPath) {
        loadVendors(map, csvPath, VendorService.LocalVendor.class);
    }

    private static <T extends VendorService.Vendor> void loadVendors(Map<String, T> map, String csvPath, Class<T> clazz) {
        try (BufferedReader br = new BufferedReader(new FileReader(csvPath))) {
            String line;
            String[] headers = null;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    headers = line.split(",");
                    firstLine = false;
                    continue;
                }
                String[] parts = line.split(",");
                T vendor = clazz.getDeclaredConstructor().newInstance();
                String usernameKey = null;
                for (int i = 0; i < headers.length && i < parts.length; i++) {
                    String key = headers[i].trim();
                    String value = parts[i].trim();
                    switch (key) {
                        case "vendor_id": vendor.setVendorId(value); break;
                        case "username": usernameKey = value; break;
                        case "name": vendor.setName(value); break;
                        case "level": vendor.setLevel(value); break;
                        case "status": vendor.setStatus(value); break;
                        case "children": vendor.setChildrenUsernames(parseList(value)); break;
                        case "permissions": vendor.setPermissions(parseList(value)); break;
                        case "access_control": vendor.setAccessControl(parseMap(value)); break;
                        case "phone": vendor.setPhone(value); break;
                    }
                }
                if (usernameKey != null && !usernameKey.isEmpty()) {
                    map.put(usernameKey, vendor);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<String> parseList(String value) {
        if (value == null || value.isEmpty() || value.equals("[]")) return null;
        value = value.replaceAll("[\\[\\]\"]", "");
        String[] arr = value.split(",");
        List<String> list = new ArrayList<>();
        for (String s : arr) {
            if (!s.trim().isEmpty()) list.add(s.trim());
        }
        return list;
    }

    private static Map<String, Object> parseMap(String value) {
        if (value == null || value.isEmpty() || value.equals("{}")) return null;
        value = value.replaceAll("[\\{\\}\"]", "");
        String[] arr = value.split(",");
        Map<String, Object> map = new HashMap<>();
        for (String s : arr) {
            String[] kv = s.split(":");
            if (kv.length == 2) {
                map.put(kv[0].trim(), parseBooleanOrString(kv[1].trim()));
            }
        }
        return map;
    }

    private static Object parseBooleanOrString(String value) {
        if ("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)) {
            return Boolean.parseBoolean(value);
        }
        return value;
    }
}
