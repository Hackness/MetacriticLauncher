package launcher.parse;

import launcher.parse.exception.ParseFailureException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Created by Hack
 * Date: 02.09.2017 11:22
 *
 * Grabs data from Metacritic
 */
public class Grabber {
    private static final Grabber instance = new Grabber();

    public static Grabber getInstance() {
        return instance;
    }

    /**
     * Set the http.agent for website access
     */
    private Grabber() {
        System.setProperty("http.agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36     " +
                "(KHTML, like Gecko) Chrome/28.0.1500.29 Safari/537.36");
    }

    /**
     * Grab content of the presented item url
     * @param addr - url of the item
     * @return - string content
     */
    public String getContent(String addr) {
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(addr);
            try (BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()))) {
                String line;
                while ((line = in.readLine()) != null)
                    sb.append(line).append("\n");
            }
        } catch (Exception e) {
            throw new ParseFailureException("Error while parsing the url " + addr, e);
        }
        return sb.toString();
    }
}
