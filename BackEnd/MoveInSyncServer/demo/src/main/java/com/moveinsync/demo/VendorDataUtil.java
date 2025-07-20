package com.moveinsync.demo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VendorDataUtil {
    public static List<Map<String, String>> getVendorData(String vendorType, String username) {
        String fileName = "databases/" + vendorType + "_vendors.csv";
        List<Map<String, String>> result = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
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
                if (parts.length == headers.length) {
                    Map<String, String> row = new HashMap<>();
                    for (int i = 0; i < headers.length; i++) {
                        row.put(headers[i].trim(), parts[i].trim());
                    }
                    if (username == null || username.equals(row.get("username"))) {
                        result.add(row);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
