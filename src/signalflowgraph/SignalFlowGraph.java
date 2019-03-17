/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package signalflowgraph;

import java.io.IOException;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import signalflowgraph.logic.Graph;
import signalflowgraph.logic.GraphDrawer;

/**
 *
 * @author arabtech
 */
public class SignalFlowGraph extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root1;
        root1 = FXMLLoader.load(getClass().getResource("views/FlowGraphGui.fxml"));
        Stage stage = new Stage();
        stage.setTitle("ABC");
        stage.setScene(new Scene(root1));
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
