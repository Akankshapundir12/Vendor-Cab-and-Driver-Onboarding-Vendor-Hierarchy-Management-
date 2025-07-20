package com.moveinsync.demo;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    static {
        System.out.println("Get all your API docs here: http://localhost:8080/swagger-ui/index.html");
    }

    public enum VendorType {
        SUPER("super"),
        REGIONAL("regional"),
        CITY("city"),
        LOCAL("local");
        private final String type;
        VendorType(String type) { this.type = type; }
        public String getType() { return type; }
        public static VendorType fromString(String value) {
            for (VendorType v : VendorType.values()) {
                if (v.type.equalsIgnoreCase(value)) return v;
            }
            throw new IllegalArgumentException("Invalid vendor type: " + value);
        }
    }

    @PostMapping("/vendor/data")
    public ResponseEntity<Object> getVendorData(@RequestBody VendorDataRequest request) {
        VendorType vendorType;
        try {
            vendorType = VendorType.fromString(request.getVendorType());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse("Invalid vendor type. Allowed: super, regional, city, local"));
        }
        List<java.util.Map<String, String>> data = VendorDataUtil.getVendorData(vendorType.getType(), request.getUsername());
        return ResponseEntity.ok(data);
    }

    public static class VendorDataRequest {
        private String username;
        private String vendorType;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getVendorType() {
            return vendorType;
        }

        public void setVendorType(String vendorType) {
            this.vendorType = vendorType;
        }
    }
    @PostMapping("/signup")
    public ResponseEntity<Object> signup(@RequestBody SignupRequest signupRequest) {
        System.out.println("Signup attempt: " + signupRequest);
        System.out.println("Raw request body: " + signupRequest);
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            String json = mapper.writeValueAsString(signupRequest);
            System.out.println("Signup JSON: " + json);
        } catch (Exception e) {
            System.out.println("Error serializing signup request to JSON: " + e.getMessage());
        }
        if(signupRequest.getUsername() == null || signupRequest.getUsername().isEmpty() ||
            signupRequest.getPassword() == null || signupRequest.getPassword().isEmpty() ||
            signupRequest.getName() == null || signupRequest.getName().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse("Username, name, and password cannot be empty"));
        }
        if (CsvUserDatabase.addUser(signupRequest.getUsername(), signupRequest.getName(), signupRequest.getPassword())) {
            String vendorType = getVendorTypeForUser(signupRequest.getUsername());
            VendorTypeResponse response = new VendorTypeResponse("Signup successful", vendorType, signupRequest.getUsername(), signupRequest.getName());
            try {
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                String json = mapper.writeValueAsString(response);
                System.out.println("Signup API Response: " + json);
            } catch (Exception e) {
                System.out.println("Error serializing signup response to JSON: " + e.getMessage());
            }
            return ResponseEntity.ok(response);
        } else {
            MessageResponse errorResponse = new MessageResponse("User already exists or error occurred");
            try {
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                String json = mapper.writeValueAsString(errorResponse);
                System.out.println("Signup API Response: " + json);
            } catch (Exception e) {
                System.out.println("Error serializing signup error response to JSON: " + e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    // DTO for signup request
    public static class SignupRequest {
        private String username;
        private String password;
        private String name;
        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
        public void setname(String name) {
            this.name = name;
        }
        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return "SignupRequest{" +
                    "username='" + username + '\'' +
                    ", password='" + password + '\'' +
                    '}';
        }
    }
    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginRequest loginRequest) {
        System.out.println("Login attempt: " + loginRequest);
        System.out.println("Raw request body: " + loginRequest);
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            String json = mapper.writeValueAsString(loginRequest);
            System.out.println("Login JSON: " + json);
        } catch (Exception e) {
            System.out.println("Error serializing login request to JSON: " + e.getMessage());
        }
        if(loginRequest.getUsername() == null || loginRequest.getUsername().isEmpty() ||
            loginRequest.getPassword() == null || loginRequest.getPassword().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse("Username and password cannot be empty"));
        }
        if (CsvUserDatabase.validateUser(loginRequest.getUsername(), loginRequest.getPassword())) {
            String vendorType = getVendorTypeForUser(loginRequest.getUsername());
            CsvUserDatabase.UserData user = CsvUserDatabase.getUser(loginRequest.getUsername());
            String name = user != null ? user.getName() : "";
            VendorTypeResponse response = new VendorTypeResponse("Login successful", vendorType, loginRequest.getUsername(), name);
            try {
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                String json = mapper.writeValueAsString(response);
                System.out.println("Login API Response: " + json);
            } catch (Exception e) {
                System.out.println("Error serializing login response to JSON: " + e.getMessage());
            }
            return ResponseEntity.ok(response);
        } else {
            MessageResponse errorResponse = new MessageResponse("Invalid credentials");
            try {
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                String json = mapper.writeValueAsString(errorResponse);
                System.out.println("Login API Response: " + json);
            } catch (Exception e) {
                System.out.println("Error serializing login error response to JSON: " + e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    // DTO for login request
    public static class LoginRequest {
        @Override
        public String toString() {
            return "LoginRequest{" +
                    "username='" + username + '\'' +
                    ", password='" + password + '\'' +
                    '}';
        }
        private String username;
        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    // DTO for JSON response
    public static class MessageResponse {
        private String message;

        public MessageResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
    @org.springframework.beans.factory.annotation.Autowired
    private VendorService vendorService;

    private String getVendorTypeForUser(String username) {
        if (vendorService.getSuperVendors().containsKey(username)) return "super";
        if (vendorService.getRegionalVendors().containsKey(username)) return "regional";
        if (vendorService.getCityVendors().containsKey(username)) return "city";
        if (vendorService.getLocalVendors().containsKey(username)) return "local";
        return "Yet to be Assigned";
    }

    public static class VendorTypeResponse {
        private String message;
        private String vendor_type;
        private String username;
        private String email;

        public VendorTypeResponse(String message, String vendor_type, String username, String name) {
            this.message = message;
            this.vendor_type = vendor_type;
            this.email = username;
            this.username = name;
        }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public String getVendor_type() { return vendor_type; }
        public void setVendor_type(String vendor_type) { this.vendor_type = vendor_type; }
        public String getUsername() { return email; }
        public void setUsername(String username) {this.email = username;}
        public String getName() { return username; }
        public void setName(String name) {
            this.username = name;
        }

    }
}
