package launcher.gui;

import launcher.data.DataManager;
import launcher.data.Game;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import launcher.Main;
import launcher.ThreadPoolManager;

import java.awt.*;
import java.net.URI;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ScheduledFuture;

/**
 * Created by Hack
 * Date: 02.09.2017 12:20
 *
 * Controller of the main window
 */
public class MainController implements Initializable {
    private static final MainController instance = new MainController();

    public static MainController getInstance() {
        return instance;
    }

    @FXML private TableView<Game> table;
    @FXML private TableColumn<Game, Integer> idCol;
    @FXML private TableColumn<Game, String> nameCol;
    @FXML private TableColumn<Game, Double> ratingCol;
    @FXML private TableColumn<Game, Double> userScoreCol;
    @FXML private TableColumn<Game, String> releaseCol;
    @FXML private TableColumn<Game, String> genreCol;
    @FXML private TableColumn<Game, Button> removeCol;
    @FXML private TableColumn<Game, ImageView> imageCol;
    @FXML private Button addBtn;
    @FXML private Button refreshBtn;
    @FXML private ProgressIndicator idc;
    @FXML private Label thNum;

    private ScheduledFuture<?> loadingTask;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        ratingCol.setCellValueFactory(new PropertyValueFactory<>("rating"));
        userScoreCol.setCellValueFactory(new PropertyValueFactory<>("userScore"));
        releaseCol.setCellValueFactory(new PropertyValueFactory<>("release"));
        genreCol.setCellValueFactory(new PropertyValueFactory<>("genre"));
        removeCol.setCellValueFactory(c -> {
            Button b = new Button("✕");
            b.setOnAction(a -> ThreadPoolManager.getInstance().execute(() -> DataManager.getInstance().remove(c.getValue())));
            b.setPrefWidth(31);
            b.setPrefHeight(31);
            return new SimpleObjectProperty<>(b);
        });
        imageCol.setCellValueFactory(c -> {
            String url = c.getValue().getImgUrl();
            if (url == null)
                return new SimpleObjectProperty<>();
            ImageView view = new ImageView(url);
            view.setFitWidth(50);
            view.setFitHeight(50);
            return new SimpleObjectProperty<>(view);
        });
        table.getColumns().forEach(c -> c.setStyle("-fx-alignment: BASELINE-CENTER; -fx-font-size:13px;"));
        addBtn.setOnAction(e -> Main.showAddPage());
        refreshBtn.setOnAction(e -> DataManager.getInstance().updateAllInfo());
        idc.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
        ThreadPoolManager.getInstance().runDaemon(() -> Platform.runLater(() ->
                thNum.setText(ThreadPoolManager.getInstance().activeCount() + "")), 100);
        table.setRowFactory( tv -> {
            TableRow<Game> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    Game rowData = row.getItem();
                    try {
                        Desktop.getDesktop().browse(new URI(rowData.getUrl()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            return row;
        });
    }

    /**
     * Refresh table elements by existing data
     */
    public void refresh() {
        table.getItems().clear();
        table.getItems().addAll(DataManager.getInstance().getData());
    }

    /**
     * Makes loading indicator visible. Also starting a task that is checking count of an active threads.
     * If there is no active threads, stopping the task, hiding indicator, storing and refreshing updated data
     */
    public void loading() {
        if (!idc.isVisible())
            idc.setVisible(true);
        if (loadingTask == null || loadingTask.isCancelled() || loadingTask.isDone())
            loadingTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(() -> {
                if (ThreadPoolManager.getInstance().activeCount() <= 1) {
                    idc.setVisible(false);
                    loadingTask.cancel(false);
                    DataManager.getInstance().serialize();
                    refresh();
                }
            }, 100, 100);
    }
}
