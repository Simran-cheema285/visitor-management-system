import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public class VisitorService {
    private static final String FILE_PATH = "visitors.json";
    private static final Gson gson = new Gson();
    private static List<Visitor> visitors = new ArrayList<>();

    public static void addVisitor(Visitor visitor) {
        visitors.add(visitor);
        saveVisitors();
        System.out.println("✅ Visitors saved to file: " + visitors.size());
    }

    public static List<Visitor> getAllVisitors() {
        return readVisitorsFromFile("visitors.json");
    }


    public static List<Visitor> searchVisitorByName(String name) {
        List<Visitor> matched = new ArrayList<>();
        for (Visitor v : visitors) {
            if (v.getName().equalsIgnoreCase(name)) {
                matched.add(v);
            }
        }
        return matched;
    }

    public static boolean logoutVisitorByMobile(String mobile) {
        List<Visitor> visitors = readVisitorsFromFile("visitors.json");
        boolean updated = false;

        for (Visitor v : visitors) {
            if (v.getMobile() != null &&
                    v.getMobile().equalsIgnoreCase(mobile) &&
                    v.getLogoutTime() == null) {

                v.setLogoutTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                updated = true;
                break;
            }
        }

        if (updated) {
            writeVisitorsToFile(visitors,"visitors.json");
        }

        return updated;
    }

    public static List<Host> readHosts(String filename) {
        try (Reader reader = new FileReader(filename)) {
            Type hostListType = new TypeToken<List<Host>>() {}.getType();
            return new Gson().fromJson(reader, hostListType);
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    public static void writeHosts(String filename, List<Host> hosts) {
        try (Writer writer = new FileWriter(filename)) {
            new GsonBuilder().setPrettyPrinting().create().toJson(hosts, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Visitor> readVisitorsFromFile(String fileName) {
        try (Reader reader = new FileReader(fileName)) {
            Type listType = new TypeToken<List<Visitor>>() {}.getType();
            List<Visitor> visitors = gson.fromJson(reader, listType);
            return visitors != null ? visitors : new ArrayList<>();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    public static void writeVisitorsToFile(List<Visitor> visitors, String fileName) {
        try (Writer writer = new FileWriter(fileName)) {
            gson.toJson(visitors, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static List<Visitor> readVisitors(String filename) throws IOException {
        Reader reader = new FileReader(filename);
        Type visitorListType = new TypeToken<List<Visitor>>() {}.getType();
        List<Visitor> visitors = new Gson().fromJson(reader, visitorListType);
        reader.close();
        return visitors != null ? visitors : new ArrayList<>();
    }

    public static void writeVisitors(String filename, List<Visitor> visitors) throws IOException {
        Writer writer = new FileWriter(filename);
        new GsonBuilder().setPrettyPrinting().create().toJson(visitors, writer);
        writer.close();
    }


    public static void saveVisitors() {
        try (Writer writer = new FileWriter(FILE_PATH)) {
            gson.toJson(visitors, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadVisitors() {
        try (Reader reader = new FileReader(FILE_PATH)) {
            Type listType = new TypeToken<List<Visitor>>() {}.getType();
            List<Visitor> loadedVisitors = gson.fromJson(reader, listType);
            if (loadedVisitors != null) {
                visitors = loadedVisitors;
            }
        } catch (IOException e) {
            visitors = new ArrayList<>();
        }
    }
}
