import com.sun.net.httpserver.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

public class MainServer {
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/register", exchange -> {
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

            if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
                exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
                exchange.sendResponseHeaders(204, -1);
                return;
            }
            
            String body;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))) {
                body = reader.lines().collect(Collectors.joining("\n"));
            }
            String name = extractJsonValue(body, "name");
            String contact = extractJsonValue(body, "contact");
            String issue = extractJsonValue(body, "issue");

            String response;
            int statusCode = 200;

            if (name.isEmpty() || contact.isEmpty() || issue.isEmpty()) {
                response = "{\"error\": \"Please fill all fields!\"}";
                statusCode = 400;
            } else {
                Complaint newComplaint = ComplaintService.register(name, contact, issue);
                if (newComplaint != null) {
                    response = "{\"message\": \"Complaint Registered! ID: " + newComplaint.id + "\"}";
                } else {
                    response = "{\"error\": \"Failed to register complaint.\"}";
                    statusCode = 500;
                }
            }

            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(statusCode, response.getBytes().length);
            exchange.getResponseBody().write(response.getBytes());
            exchange.close();
        });

        server.createContext("/search", exchange -> {
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            String query = exchange.getRequestURI().getQuery();
            String response;
            int statusCode = 200;

            try {
                if (query == null || !query.startsWith("id=") || query.split("=").length < 2) {
                    response = "{\"error\": \"Complaint Not Found! Please provide an ID.\"}";
                    statusCode = 400;
                } else {
                    String idStr = query.split("=")[1];
                    int id = Integer.parseInt(idStr);
                    Complaint complaint = ComplaintService.search(id);
                    if (complaint != null) {
                        response = complaintToJson(complaint);
                    } else {
                        response = "{\"error\": \"Complaint Not Found!\"}";
                        statusCode = 404;
                    }
                }
            } catch (NumberFormatException e) {
                response = "{\"error\": \"Invalid ID format. Please enter a numeric ID.\"}";
                statusCode = 400;
            }

            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(statusCode, response.getBytes().length);
            exchange.getResponseBody().write(response.getBytes());
            exchange.close();
        });

        server.createContext("/all", exchange -> {
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            List<Complaint> complaints = ComplaintService.getAll();
            
            StringBuilder jsonBuilder = new StringBuilder();
            jsonBuilder.append("[");
            for (int i = 0; i < complaints.size(); i++) {
                jsonBuilder.append(complaintToJson(complaints.get(i)));
                if (i < complaints.size() - 1) {
                    jsonBuilder.append(",");
                }
            }
            jsonBuilder.append("]");
            String response = jsonBuilder.toString();

            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.getBytes().length);
            exchange.getResponseBody().write(response.getBytes());
            exchange.close();
        });
        server.createContext("/", exchange -> serveFile(exchange, "index.html", "text/html"));
        server.createContext("/style.css", exchange -> serveFile(exchange, "style.css", "text/css"));
        server.createContext("/script.js", exchange -> serveFile(exchange, "script.js", "application/javascript"));


        System.out.println("Server running. Open http://localhost:8080 in your browser.");
        server.start();
    }

    private static void serveFile(HttpExchange exchange, String filePath, String contentType) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            String response = "404 (Not Found)";
            exchange.sendResponseHeaders(404, response.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
            return;
        }
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.sendResponseHeaders(200, file.length());
        try (OutputStream os = exchange.getResponseBody()) {
            Files.copy(file.toPath(), os);
        }
    }

    public static String extractJsonValue(String json, String key) {
        String pattern = "\"" + key + "\":";
        int start = json.indexOf(pattern);
        if (start == -1) return "";
        
        start += pattern.length();
        while (start < json.length() && (json.charAt(start) == ' ' || json.charAt(start) == '"')) {
            start++;
        }
        
        int end = json.indexOf('"', start);
        if (end == -1) return "";
        
        return json.substring(start, end);
    }
    public static String complaintToJson(Complaint c) {
        return String.format(
            "{\"id\": %d, \"name\": \"%s\", \"contact\": \"%s\", \"issue\": \"%s\", \"status\": \"%s\"}",
            c.id, 
            escapeJson(c.name), 
            escapeJson(c.contact), 
            escapeJson(c.issue), 
            escapeJson(c.status)
        );
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
