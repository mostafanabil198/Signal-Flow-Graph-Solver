/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package signalflowgraph.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import signalflowgraph.logic.Graph;
import signalflowgraph.logic.GraphDrawer;

/**
 * FXML Controller class
 *
 * @author arabtech
 */
public class FlowGraphGuiController implements Initializable {

    private int size;
    private Graph g;
    private GraphDrawer gd;

    @FXML
    AnchorPane numOfNodesA, enterEdgesA, resultsA;

    @FXML
    TextField numOfNodesTF, fromTF, toTF, gainTF;

    @FXML
    Label transferFunL, errorLbl;

    @FXML
    TextArea forwardPathsTA, loopsTA, nonLoopsTA, deltasTA;

    @FXML
    public void setNumberOfNodes() {
        String nodes = numOfNodesTF.getText();
        try {
            size = Integer.parseInt(nodes);
            g = new Graph(size);
            numOfNodesA.setVisible(false);
            enterEdgesA.setVisible(true);
            errorLbl.setText("");
        } catch (Exception e) {
            errorLbl.setText("Wrong Input!, Enter integer number");
        }
    }

    @FXML
    public void addEdge() {
        try {
            int fromN = Integer.parseInt(fromTF.getText());
            int toN = Integer.parseInt(toTF.getText());
            int gainN = Integer.parseInt(gainTF.getText());
            if (fromN >= g.getSize() || fromN < 0 || toN >= g.getSize() || toN < 0) {
                errorLbl.setText("Wrong Input!, Node number is out of bound");
            } else {
                g.addEdge(fromN, toN, gainN);
                fromTF.clear();
                toTF.clear();
                gainTF.clear();
                errorLbl.setText("");
            }
        } catch (Exception e) {
            errorLbl.setText("Wrong Input!, Enter integer number");
        }
    }

    @FXML
    public void showResult() {
        enterEdgesA.setVisible(false);
        String result = g.getTransferFunction(0, size - 1);
        resultsA.setVisible(true);
        String paths = "";
        System.out.println(g.getPathList().size());
        for (int i = 0; i < g.getPathList().size(); i++) {
            paths += g.getPathList().get(i) + "\n";
        }
        forwardPathsTA.setText(paths);
        String cycles = "";
        for (int i = 0; i < g.getCycles().size(); i++) {
            cycles += g.getCycles().get(i) + "\n";
        }
        loopsTA.setText(cycles);
        nonLoopsTA.setText(g.getNonTouchingToPrint());
        transferFunL.setText(result);
        String deltas = "Delta = ";
        deltas += g.getDenominator();
        deltas += "\n";
        for (int i = 0; i < g.getDeltas().length; i++) {
            deltas += "delta " + i + " = " + g.getDeltas()[i];
        }
        deltasTA.setText(deltas);

    }

    @FXML
    public void showGraph() {
        gd = new GraphDrawer(size, g.getGraph());
        Stage stage = new Stage();
        stage.setTitle("My New Stage Title");
        stage.setScene(new Scene(gd.getGroup(), 1800, 800));
        stage.show();
    }

    @FXML
    public void exit() {
        System.exit(0);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        numOfNodesA.setVisible(true);
        enterEdgesA.setVisible(false);
        resultsA.setVisible(false);
        numOfNodesTF.clear();
        fromTF.clear();
        toTF.clear();
        gainTF.clear();
        errorLbl.setText("");
    }

}
