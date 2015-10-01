package main.java.yaddns;

import org.json.JSONObject;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;
import java.util.concurrent.Executors;

import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Logger;

/**
 * Created by anton on 30.09.2015.
 */
public class Application {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static Logger log = Logger.getLogger(Application.class.getName());
    private final String path = System.getenv("APPDATA") + "\\YaDDNS\\";
    private final String fileName = "YaDDNS.json";

    public static void main(String[] args) {
        Application application = new Application();
        application.init();
    }

    public void init() {
        getFile();
        //scheduler.scheduleAtFixedRate(new Updater(url, token), 20, 20, TimeUnit.SECONDS);
        //log.info("Initialisation success");
    }

    private boolean getFile() {
        File file = new File(path + fileName);
        try {
            //проверяем, что если файл не существует то создаем его
            if(!file.exists()){
                log.info("Settings File not found.");
                if (file.getParentFile().mkdir()) {
                    if (file.createNewFile()) {
                        JSONObject json = getJson();

                        //PrintWriter обеспечит возможности записи в файл
                        PrintWriter out = new PrintWriter(file.getAbsoluteFile());
                        try {
                            out.print(json.toString());
                        } finally {
                            out.close();
                        }
                        log.info("Settings File added.");
                    } else {
                        log.info("Error: Settings File not added.");
                    }
                }
            } else {
                log.info("YES");
            }
        } catch(Exception e) {
            log.severe(e.getMessage());
        }

        return false;
    }

    private String readStreamToString(InputStream in, String encoding)
            throws IOException {
        StringBuffer b = new StringBuffer();
        InputStreamReader r = new InputStreamReader(in, encoding);
        int c;
        while ((c = r.read()) != -1) {
            b.append((char)c);
        }
        return b.toString();
    }

    private JSONObject getJson() {
        JSONObject json = new JSONObject();
        Scanner console = new Scanner(System.in);
        String url;
        String token;

        boolean fine = false;
        while (!fine) {
            System.out.println("url:");
            url = console.nextLine();
            System.out.println("token:");
            token = console.nextLine();
            try {
                URLConnection conn = new URL(url + "?token=" + token).openConnection();
                conn.setConnectTimeout(5 * 1000);
                conn.connect();
                String html = readStreamToString(conn.getInputStream(), "UTF-8");
                log.info(html);
                if (html.equalsIgnoreCase("OK")) {
                    fine = true;
                }
            } catch (MalformedURLException e) {
                log.severe("Malformed url '" + url + "?token=" + token + "': " + e.getMessage());
                fine = false;
            } catch (IOException e) {
                log.severe("Could not open url '" + url + "?token=" + token + "': " + e.getMessage());
                fine = false;
            }

            if (fine) {
                json.put("url", url);
                json.put("token", token);
            }
        }

        return json;
    }
}
