package launcher.gui;

import data.DataManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import launcher.ThreadPoolManager;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Hack
 * Date: 03.09.2017 5:17
 *
 * Controller of the window that add a new item
 */
public class AddController implements Initializable {
    @FXML private TextField url;
    @FXML private Button okBtn;
    @FXML private Button cancelBtn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        okBtn.setOnAction(a -> {
            ThreadPoolManager.getInstance().execute(() -> DataManager.getInstance().add(url.getText()));
            close();
        });
        cancelBtn.setOnAction(a -> close());
    }

    private void close() {
        ((Stage) cancelBtn.getScene().getWindow()).close();
    }
}
