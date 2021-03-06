package yaddns;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Logger;

/**
 * Created by anton on 01.10.2015.
 */
class Updater implements Runnable {

    private static final Logger log = Logger.getLogger(Runnable.class.getName());
    private String url;

    private String token;

    public Updater(String url, String token) {
        this.url = url;
        this.token = token;
    }

    @Override
    public void run() {
        String threadName = Thread.currentThread().getName();
        try {
            URLConnection conn = new URL(url + "?token=" + token).openConnection();
            conn.setConnectTimeout(5 * 1000);
            conn.connect();
            String html = Application.readStreamToString(conn.getInputStream(), "UTF-8");
            log.info(html);
        } catch (MalformedURLException e) {
            log.severe(threadName + ": Malformed url '" + url + "?token=" + token + "': " + e.getMessage());
        } catch (IOException e) {
            log.severe(threadName + ": Could not open url '" + url + "?token=" + token + "': " + e.getMessage());
        }
    }
}
