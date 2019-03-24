/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package signalflowgraph.logic;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.QuadCurve;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Pair;

/**
 *
 * @author arabtech
 */
public class GraphDrawer {

    private int size;
    private ArrayList<Circle> nodes;
    private Group group;
    private List<List<Pair<Integer, Integer>>> g;
    private boolean[][] draw;

    public GraphDrawer(int size, List<List<Pair<Integer, Integer>>> g) {
        this.size = size;
        this.g = g;
        draw = new boolean[size][size];
        nodes = new ArrayList<>();
        group = new Group();
        drawAllNodes();
        drawAllEdges();

    }

    public Group getGroup() {
        return group;
    }

    private void drawNode(int x, String name) {
        Circle c = new Circle(x, 400, 20);
        Text text = new Text(name);
        text.setX(x - 5);
        text.setY(400 + 5);
        text.setFill(Color.WHITE);
        group.getChildren().add(c);
        nodes.add(c);
        group.getChildren().add(text);
    }

    private void drawCurvedEdge(double sX, double sY, double eX, double eY, double cX, double cY, String gain, boolean up) {
        QuadCurve edge = new QuadCurve(sX, sY, cX, cY, eX, eY);
        edge.setFill(Color.TRANSPARENT);
        edge.setStroke(Color.BLACK);
        Text text = new Text(gain);
        text.setX(cX);
        text.setY(cY);
        text.setFill(Color.RED);
        text.setFont(new Font(20));
        text.setStroke(Color.RED);
        text.setStrokeWidth(2);
        group.getChildren().add(edge);
        group.getChildren().add(text);
        group.getChildren().add(verticalArrow(eX, eY, up));
    }

    private void drawLineEdge(double sX, double sY, double eX, double eY, String gain, boolean right) {
        Line edge = new Line(sX, sY, eX, eY);
        edge.setStroke(Color.BLACK);
        Text text = new Text(gain);
        text.setX((sX + eX) / 2);
        text.setY(sY - 5);
        text.setFill(Color.RED);
        text.setFont(new Font(20));
        text.setStroke(Color.RED);
        text.setStrokeWidth(2);
        group.getChildren().add(edge);
        group.getChildren().add(text);
        group.getChildren().add(horizontalArrow(eX, eY, right));
    }

    private void drawCircleEdge(double sX, double sY, String gain) {
        Circle edge = new Circle(sX, sY, 10);
        edge.setFill(Color.TRANSPARENT);
        edge.setStroke(Color.BLACK);
        Text text = new Text(gain);
        text.setX(sX + 8);
        text.setY(sY);
        text.setFill(Color.RED);
        text.setFont(new Font(20));
        text.setStroke(Color.RED);
        text.setStrokeWidth(2);
        group.getChildren().add(edge);
        group.getChildren().add(text);
    }

    private Polygon verticalArrow(double x, double y, boolean up) {
        Polygon arrow = new Polygon();
        arrow.setStroke(Color.RED);
        arrow.setStrokeWidth(4);
        arrow.setFill(Color.RED);
        if (up) {
            arrow.getPoints().addAll(new Double[]{
                x - 3, y,
                x, y + 3,
                x - 6, y + 3});
        } else {
            arrow.getPoints().addAll(new Double[]{
                x - 3, y,
                x, y - 3,
                x - 6, y - 3});
        }
        return arrow;
    }

    private Polygon horizontalArrow(double x, double y, boolean right) {
        Polygon arrow = new Polygon();
        arrow.setStroke(Color.RED);
        arrow.setStrokeWidth(4);
        arrow.setFill(Color.RED);
        if (right) {
            arrow.getPoints().addAll(new Double[]{
                x, y,
                x - 3, y - 3,
                x - 3, y + 3});
        } else {
            x += 40;
            arrow.getPoints().addAll(new Double[]{
                x, y,
                x + 3, y - 3,
                x + 3, y + 3});
        }
        return arrow;
    }

    private void drawAllNodes() {
        for (int i = 0; i < size; i++) {
            drawNode(100 + i * 100, String.valueOf(i));
        }
    }

    private void drawAllEdges() {
        int z = 0;
        for (int i = 0; i < g.size(); i++) {
            for (int y = 0; y < g.get(i).size(); y++) {
                double sX = nodes.get(i).getCenterX();
                double sY = nodes.get(i).getCenterY();
                double eX = nodes.get(g.get(i).get(y).getKey()).getCenterX();
                double eY = nodes.get(g.get(i).get(y).getKey()).getCenterY();
                if (Math.abs(i - g.get(i).get(y).getKey()) == 1 && draw[g.get(i).get(y).getKey()][i] == false) {
                    sX += 20;
                    eX -= 20;
                    int gain = g.get(i).get(y).getValue();
                    drawLineEdge(sX, sY, eX, eY, String.valueOf(gain), i < g.get(i).get(y).getKey());
                    draw[i][g.get(i).get(y).getKey()] = true;
                } else if (i - g.get(i).get(y).getKey() == 0) {
                    int gain = g.get(i).get(y).getValue();
                    drawCircleEdge(sX + 30, sY, String.valueOf(gain));
                    draw[i][g.get(i).get(y).getKey()] = true;
                } else {
                    double cX = (sX + eX) / 2;
                    double cY = 400 + Math.pow(-1, z) * Math.abs(sX - eX) * .5;
                    int gain = g.get(i).get(y).getValue();
                    drawCurvedEdge(sX, sY + Math.pow(-1, z) * 20, eX, eY + Math.pow(-1, z) * 20, cX, cY, String.valueOf(gain), Math.pow(-1, z) == 1);
                    draw[i][g.get(i).get(y).getKey()] = true;
                    z++;
                }
            }
        }
    }

}
