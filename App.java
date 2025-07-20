

import static spark.Spark.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.*;

public class App {
    static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void main(String[] args) {
        port(4567);
        VisitorService.loadVisitors();

        before((req, res) -> res.header("Access-Control-Allow-Origin", "*"));
        options("/*", (req, res) -> {
            res.header("Access-Control-Allow-Headers", req.headers("Access-Control-Request-Headers"));
            res.header("Access-Control-Allow-Methods", req.headers("Access-Control-Request-Method"));
            return "OK";
        });

        //  Save to pendingVisitors.json with image
        post("/api/visitor", (req, res) -> {
            Visitor visitor = gson.fromJson(req.body(), Visitor.class);

            if (visitor.getTimestamp() == null || visitor.getTimestamp().isEmpty()) {
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                visitor.setTimestamp(timestamp);
            }

            System.out.println("Received Visitor: " + visitor.getName());

            List<Visitor> pendingVisitors = VisitorService.readVisitorsFromFile("pendingvisitors.json");
            pendingVisitors.add(visitor);
            VisitorService.writeVisitorsToFile(pendingVisitors, "pendingvisitors.json");

            res.status(201);
            return "Visitor request submitted. Waiting for host approval.";
        });

        // Explicit Visitor Request API (if you're using this separately)
        post("/api/visitor/request", (req, res) -> {
            Visitor visitor = gson.fromJson(req.body(), Visitor.class);
            List<Visitor> pendingVisitors = VisitorService.readVisitorsFromFile("pendingvisitors.json");
            pendingVisitors.add(visitor);
            VisitorService.writeVisitorsToFile(pendingVisitors, "pendingvisitors.json");
            res.status(200);
            return "Visitor request submitted. Waiting for host approval.";
        });

        // View all approved visitors
        get("/api/visitors", (req, res) -> {
            res.type("application/json");
            List<Visitor> visitors = VisitorService.getAllVisitors();

            List<Visitor> validVisitors = visitors.stream()
                    .filter(v -> {
                        // Keep if not approved OR (visitDate is today or future)
                        if (!v.getApproved()) return true;

                        try {
                            return LocalDate.parse(v.getVisitDate()).compareTo(LocalDate.now()) >= 0;
                        } catch (Exception e) {

                            return true;
                        }
                    })
                    .collect(Collectors.toList());

            return gson.toJson(validVisitors);
        });


        //  Host checks pending requests (based on hostEmail)
        get("/api/host/pending", (req, res) -> {
            res.type("application/json");
            String hostEmail = req.queryParams("hostEmail");

            if (hostEmail == null || hostEmail.isEmpty()) {
                res.status(400);
                return "Host email is required";
            }

            List<Visitor> pendingVisitors = VisitorService.readVisitorsFromFile("pendingvisitors.json");

            List<Visitor> hostPendingVisitors = pendingVisitors.stream()
                    .filter(v -> v.getHostEmail() != null && v.getHostEmail().equalsIgnoreCase(hostEmail))
                    .collect(Collectors.toList());

            return gson.toJson(hostPendingVisitors);
        });

        //  Visitor logs out using mobile
        post("/api/visitor/logout", (req, res) -> {
            VisitorService.loadVisitors();
            String mobile = req.queryParams("mobile");
            boolean success = VisitorService.logoutVisitorByMobile(mobile);



            if (success) {
                res.status(200);
                return gson.toJson("Visitor logged out");
            } else {
                res.status(404);
                return gson.toJson("Visitor not found or already logged out");
            }
        });
        post("/api/visitor/approve", (request, response) -> {
            String email = request.queryParams("email");
            String purpose = request.queryParams("purpose");

            List<Visitor> pendingVisitors = VisitorService.readVisitors("pendingvisitors.json");
            List<Visitor> visitors = VisitorService.readVisitors("visitors.json");

            boolean found = false;

            Iterator<Visitor> iterator = pendingVisitors.iterator();
            while (iterator.hasNext()) {
                Visitor visitor = iterator.next();
                if (visitor.getEmail().trim().equalsIgnoreCase(email.trim()) &&
                        visitor.getPurpose().trim().equalsIgnoreCase(purpose.trim())) {

                    visitor.setApproved(true);
                    visitor.setTimestamp(LocalDateTime.now().toString());

                    //  Generate simplified QR-like code
                    String qrImage = QRGenerator.generateQRCodeImageBase64(visitor.getEmail() + " - " + visitor.getPurpose());
                    visitor.setQrCode(qrImage);

                    visitors.add(visitor);        // Move to approved list
                    iterator.remove();            // Remove from pending list
                    found = true;
                    break;
                }
            }

            if (found) {
                VisitorService.writeVisitors("pendingvisitors.json", pendingVisitors);
                VisitorService.writeVisitors("visitors.json", visitors);

                response.status(200);
                return "Visitor approved successfully.";
            } else {
                response.status(404);
                return "Visitor not found in pending list.";
            }
        });

        post("/api/visitor/reject", (request, response) -> {
            String email = request.queryParams("email");
            String purpose = request.queryParams("purpose");

            List<Visitor> pendingVisitors =VisitorService.readVisitors("pendingvisitors.json");

            boolean removed = pendingVisitors.removeIf(v ->
                    v.getEmail().equalsIgnoreCase(email) &&
                            v.getPurpose().equalsIgnoreCase(purpose)
            );

            if (removed) {
                VisitorService.writeVisitors("pendingvisitors.json", pendingVisitors);
                response.status(200);
                return "Visitor rejected successfully.";
            } else {
                response.status(404);
                return "Visitor not found in pending list.";
            }
        });


        post("/api/host/register", (req, res) -> {
            Host host = gson.fromJson(req.body(), Host.class);
            List<Host> hosts = VisitorService.readHosts("hosts.json");

            boolean exists = hosts.stream()
                    .anyMatch(h -> h.getEmail().equalsIgnoreCase(host.getEmail()));

            if (exists) {
                res.status(409);
                return "Host already registered.";
            }

            hosts.add(host);
            VisitorService.writeHosts("hosts.json", hosts);
            res.status(201);
            return "Host registered successfully.";
        });

        post("/api/host/login", (req, res) -> {
            String email = req.queryParams("email");
            String password = req.queryParams("password");

            List<Host> hosts = VisitorService.readHosts("hosts.json");
            boolean valid = hosts.stream()
                    .anyMatch(h -> h.getEmail().equalsIgnoreCase(email) && h.getPassword().equals(password));

            if (valid) {
                res.status(200);
                return "Login successful.";
            } else {
                res.status(401);
                return "Invalid credentials.";
            }
        });

        post("/api/visitor/preapprove", (req, res) -> {
            Visitor visitor = gson.fromJson(req.body(), Visitor.class);
            visitor.setApproved(true);
            visitor.setTimestamp(LocalDateTime.now().toString());

            //  Generate QR code using  existing class
            String qrText = visitor.getName() + "_" + visitor.getEmail() + "_" + visitor.getVisitDate();
            String qrImage = QRGenerator.generateQRCodeImageBase64(qrText);
            visitor.setQrCode(qrImage);// make sure this field exists in Visitor.java

            List<Visitor> visitors = VisitorService.readVisitors("visitors.json");
            visitors.add(visitor);
            VisitorService.writeVisitors("visitors.json", visitors);
            visitor.setVisitDate(LocalDate.now().toString());

            res.status(201);
            return "Visitor pre-approved with QR.";
        });



    }
}
