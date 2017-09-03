package launcher;

import data.DataManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import launcher.gui.AddController;
import launcher.gui.MainController;

import java.io.IOException;

public class Main extends Application {
    private static Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Main.primaryStage = primaryStage;
        Parent root = loadFXML("MainPage.fxml", MainController.getInstance());
        primaryStage.setTitle("Metacritic Launcher");
        primaryStage.setScene(new Scene(root, 860, 590));
        primaryStage.setResizable(false);
        primaryStage.show();
        DataManager.getInstance().init();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        ThreadPoolManager.getInstance().shutdown();
    }

    /**
     * Shows the Add Menu
     */
    public static void showAddPage() {
        showModal(new Scene(loadFXML("AddPage.fxml", new AddController()), 385, 92), "Add Menu", true);
    }

    /**
     * Shows a modal window
     * @param scene - the scene that would be presented
     * @param title - title of the scene
     * @param wait - turn the main window to waiting?
     */
    private static void showModal(Scene scene, String title, boolean wait) {
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setTitle(title);
        stage.setScene(scene);
        stage.initOwner(primaryStage);
        stage.initModality(Modality.WINDOW_MODAL);
        if (wait)
            stage.showAndWait();
        else
            stage.show();
    }

    /**
     * Load an fxml file with custom controller
     * @param path - path to file
     * @param controller - controller of the window
     * @return - Parent obj of the loaded fxml
     */
    private static Parent loadFXML(String path, Object controller) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource(path));
            if (controller != null)
                loader.setController(controller);
            return (Parent) loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Error while loading " + path + ".fxml!", e);
        }
    }
}
