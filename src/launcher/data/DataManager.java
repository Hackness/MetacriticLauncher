package launcher.data;

import launcher.gui.MainController;
import launcher.ThreadPoolManager;
import launcher.parse.WebPage;
import lombok.Getter;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;

/**
 * Created by Hack
 * Date: 02.09.2017 14:10
 *
 * The manager helps to store and update data in the program
 */
public class DataManager {
    private static final String DIR = System.getenv("appdata") + "\\Hacknessdev\\MetacriticLauncher";
    private static final String DATA = DIR + "\\data";
    @Getter
    private ArrayList<Game> data = new ArrayList<>();

    private static final DataManager instance = new DataManager();
    public static DataManager getInstance() {
        return instance;
    }

    /**
     * Creating the directories that would be used next
     */
    private DataManager() {
        try {
            Files.createDirectories(new File(DIR).toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Add a new game to the program holder
     * @param url - metacritic url of the game
     */
    public void add(String url) {
        add(new Game(url));
    }

    /**
     * Add a new game to the program holder
     * @param game - POJO of the game
     */
    public void add(Game game) {
        MainController.getInstance().loading();
        data.add(game);
        ThreadPoolManager.getInstance().execute(() -> updateInfo(game, true));
    }

    /**
     * Remove a game from the program holder
     * @param game - POJO of the game
     */
    public void remove(Game game) {
        data.remove(game);
        data.trimToSize();
        serialize();
        MainController.getInstance().refresh();
    }

    /**
     * Serialize all inserted data to file
     */
    private void serialize() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(DATA))) {
            out.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deserialize all inserted data from file
     */
    @SuppressWarnings("unchecked")
    private void deserialize() {
        File file = new File(DATA);
        if (!file.exists())
            return;
        try (ObjectInputStream in =  new ObjectInputStream (new FileInputStream(file))) {
            data = (ArrayList<Game>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initialize of the DataManager. The method should be called before the manager will be used
     */
    public void init() {
        deserialize();
        MainController.getInstance().refresh();
        updateAllInfo();
    }

    /**
     * Downloading all data from Metacritic for each game and then refresh it in the program
     */
    public void updateAllInfo() {
        MainController.getInstance().loading();
        data.forEach(g -> ThreadPoolManager.getInstance().execute(() -> updateInfo(g, false)));
        serialize();
        MainController.getInstance().refresh();
    }

    /**
     * Downloading a data from Metacritic for presented game
     * @param game - the game that would be refreshed
     * @param save - store the changes?
     */
    private void updateInfo(Game game, boolean save) {
        WebPage.Metacritic.parse(game);
        if (save) {
            serialize();
            MainController.getInstance().refresh();
        }
    }
}
