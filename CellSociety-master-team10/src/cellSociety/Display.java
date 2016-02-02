package cellSociety;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import cell.Cell;
/**
 * Created by davidyan on 2/1/16.
 */
public class Display {
    private Scene myScene;
    private Group myRoot;
    private Button guiPlay, guiPause, guiStep, guiReload;
    private Slider guiSlider;
    private GridPane myGrid;
    private Canvas myCanvas;
    private Grid myGrid2;

    public void createScene(Stage stage) {
        myRoot = new Group();
        myScene = new Scene(myRoot, 1200, 700);

        Cell base = new Cell(0);
        myGrid2 = new Grid(base,10,10);

        myGrid = new GridPane();
        myGrid.setAlignment(Pos.CENTER);
        myGrid.setHgap(10);
        myGrid.setVgap(10);
        myGrid.setPadding(new Insets(25, 0, 0, -75));

        myCanvas = new Canvas(550,550);
        GraphicsContext gc = myCanvas.getGraphicsContext2D();

        gc.setFill(Color.DARKGRAY);
        gc.fillRect(0, 0, 550, 550);
        myGrid2.draw(myCanvas);
        myGrid.add(myCanvas, 0, 0, 8, 5);

        guiSlider = new Slider(0, 100, 50);
        guiSlider.setShowTickMarks(true);
        guiSlider.setShowTickLabels(true);
        guiSlider.setMajorTickUnit(10.0);
        guiSlider.setBlockIncrement(10.0);

        guiSlider.setMinWidth(225);

        guiPlay = new Button("Play");
        guiPause = new Button("Pause");
        guiStep = new Button("Step");
        guiReload = new Button("\u21BA");

        myGrid.add(guiPlay, 0, 6);
        myGrid.add(guiPause, 1, 6);
        myGrid.add(guiStep, 2, 6);
        myGrid.add(guiReload, 3, 6);
        myGrid.add(guiSlider, 4, 6, 5, 1);

        myRoot.getChildren().add(myGrid);
        stage.setScene(myScene);
        stage.show();
    }


}
