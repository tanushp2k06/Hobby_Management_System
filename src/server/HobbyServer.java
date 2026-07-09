package server;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import service.HobbyService;
import model.Hobby;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class HobbyServer {
    private static final int PORT = 8080;
    private HobbyService service = new HobbyService();

    public void startServer() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

            server.createContext("/", this::serveFile);
            server.createContext("/add", this::addHobby);
            server.createContext("/view", this::viewHobbies);
            server.createContext("/practice", this::practiceHobby);
            server.createContext("/delete", this::deleteHobby);
            server.createContext("/weekly", this::weeklyProgress);

            server.setExecutor(null);
            server.start();

            System.out.println("Server started at http://localhost:" + PORT);
        } catch (Exception e) {
            System.out.println("Server Error: " + e.getMessage());
        }
    }

    private void serveFile(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();

        if (path.equals("/")) {
            path = "/index.html";
        }

        File file = new File("web" + path);

        if (!file.exists()) {
            sendResponse(exchange, "File not found", "text/plain");
            return;
        }

        String contentType = "text/html";

        if (path.endsWith(".css")) {
            contentType = "text/css";
        } else if (path.endsWith(".js")) {
            contentType = "application/javascript";
        }

        byte[] data = java.nio.file.Files.readAllBytes(file.toPath());

        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.sendResponseHeaders(200, data.length);

        OutputStream os = exchange.getResponseBody();
        os.write(data);
        os.close();
    }

    private void addHobby(HttpExchange exchange) throws IOException {
        Map<String, String> params = getParams(exchange);

        String title = params.get("title");
        String category = params.get("category");
        String time = params.get("time");

        service.addHobby(title, category, time);

        sendResponse(exchange, "Hobby added successfully", "text/plain");
    }

    private void viewHobbies(HttpExchange exchange) throws IOException {
        ArrayList<Hobby> hobbies = service.getAllHobbies();

        StringBuilder json = new StringBuilder();
        json.append("[");

        for (int i = 0; i < hobbies.size(); i++) {
            Hobby h = hobbies.get(i);

            json.append("{");
            json.append("\"id\":").append(h.getId()).append(",");
            json.append("\"title\":\"").append(h.getTitle()).append("\",");
            json.append("\"category\":\"").append(h.getCategory()).append("\",");
            json.append("\"reminderTime\":\"").append(h.getReminderTime()).append("\",");
            json.append("\"practiced\":").append(h.isPracticed()).append(",");
            json.append("\"streak\":").append(h.getStreak());
            json.append("}");

            if (i < hobbies.size() - 1) {
                json.append(",");
            }
        }

        json.append("]");

        sendResponse(exchange, json.toString(), "application/json");
    }

    private void practiceHobby(HttpExchange exchange) throws IOException {
        Map<String, String> params = getParams(exchange);
        int id = Integer.parseInt(params.get("id"));

        service.markPracticed(id);

        sendResponse(exchange, "Hobby marked as practiced", "text/plain");
    }

    private void deleteHobby(HttpExchange exchange) throws IOException {
        Map<String, String> params = getParams(exchange);
        int id = Integer.parseInt(params.get("id"));

        service.deleteHobby(id);

        sendResponse(exchange, "Hobby deleted", "text/plain");
    }

    private void weeklyProgress(HttpExchange exchange) throws IOException {
        ArrayList<String> progress = service.getWeeklyProgress();

        StringBuilder json = new StringBuilder();
        json.append("[");

        for (int i = 0; i < progress.size(); i++) {
            json.append("\"").append(progress.get(i)).append("\"");

            if (i < progress.size() - 1) {
                json.append(",");
            }
        }

        json.append("]");

        sendResponse(exchange, json.toString(), "application/json");
    }

    private Map<String, String> getParams(HttpExchange exchange) throws UnsupportedEncodingException {
        Map<String, String> params = new HashMap<>();
        String query = exchange.getRequestURI().getQuery();

        if (query == null) {
            return params;
        }

        String[] pairs = query.split("&");

        for (String pair : pairs) {
            String[] keyValue = pair.split("=");

            if (keyValue.length == 2) {
                String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
                String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
                params.put(key, value);
            }
        }

        return params;
    }

    private void sendResponse(HttpExchange exchange, String response, String contentType) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");

        byte[] data = response.getBytes(StandardCharsets.UTF_8);

        exchange.sendResponseHeaders(200, data.length);

        OutputStream os = exchange.getResponseBody();
        os.write(data);
        os.close();
    }
}