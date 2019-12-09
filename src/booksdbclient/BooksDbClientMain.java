package booksdbclient;

import model.BookMySQLDb;
import view.BooksPane;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * Application start up.
 *
 * @author anderslm@kth.se
 */
public class BooksDbClientMain extends Application {

    @Override
    public void start(Stage primaryStage){

        BookMySQLDb booksDb = new BookMySQLDb();

        BooksPane root = new BooksPane(booksDb);

        Scene scene = new Scene(root, 800, 600);

        primaryStage.setTitle("Books Database Client");

        EventHandler closeHandler = new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                event.consume();
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to close application?");
                alert.showAndWait();
                if (alert.getResult() == ButtonType.OK) {
                    try {
                        booksDb.disconnect();
                        primaryStage.close();
                    } catch (Exception e) {}
                }
            }
        };
        primaryStage.setOnCloseRequest(closeHandler);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
