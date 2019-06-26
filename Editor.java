package TextEditor;

import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ScrollBar;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.*;

public class Editor extends Application {
    private static int WINDOW_WIDTH;
    static int WINDOW_HEIGHT;
    static double usableScreenWidth;
    static int textStartX;
    static int textStartY;
    static double scrollBarMax;
    static BlinkCursor c;
    static ScrollBar scrollBar;
    static String FILENAME;

    @Override
    public void start(Stage primaryStage){
        /* Create a Node that will be the parent of all things displayed on the screen. */
        Group root = new Group();
        /* The Scene represents the window: its height and width will be the height and width
           of the window displayed. */
        WINDOW_WIDTH = 600;
        WINDOW_HEIGHT = 600;
        textStartX = 5;
        textStartY = 0;

        Application.Parameters p = getParameters();
        FILENAME = p.getRaw().get(0);

        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT, Color.WHITE);

        /* To get information about what keys the user is pressing, create an EventHandler. */
        EventHandler<KeyEvent> keyEventHandler = new KeyEventHandler(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        /* Register the event handler to be called for all KEY_PRESSED and KEY_TYPED events. */
        scene.setOnKeyTyped(keyEventHandler);
        scene.setOnKeyPressed(keyEventHandler);

        primaryStage.setTitle("Text Editor - " + FILENAME);

        scene.setOnMouseClicked(new MouseClickEventHandler(KeyEventHandler.g));

        c = new BlinkCursor(textStartX, textStartY);
        KeyEventHandler.g.getChildren().add(c.br);
        c.makeRectangleColorChange();

        /* Make a vertical scroll bar on the right side of the screen. */
        scrollBar = new ScrollBar();
        scrollBar.setOrientation(Orientation.VERTICAL);
        /* Set the height of the scroll bar so that it fills the whole window. */
        scrollBar.setPrefHeight(WINDOW_HEIGHT);

        /* Set the range of the scroll bar. */
        scrollBarMax = 1;
        scrollBar.setMin(0);
        scrollBar.setMax(0);
        scrollBar.setValue(0);

        /* Add the scroll bar to the scene graph, so that it appears on the screen. */
        root.getChildren().add(scrollBar);

        usableScreenWidth = WINDOW_WIDTH - scrollBar.getLayoutBounds().getWidth();
        scrollBar.setLayoutX(usableScreenWidth);

        File file = new File(FILENAME);
        if (file.exists()) {
            FileReading fr = new FileReading(keyEventHandler, file);
        }


        /* When the scroll bar changes position, change the height of Text. */
        scrollBar.valueProperty().addListener(
                (ObservableValue<? extends Number> observableValue,
                 Number oldValue,
                 Number newValue) -> {

                int textHeight = KeyEventHandler.line.size() * KeyEventHandler.charHeightNow();
                int bottomLayoutY = (WINDOW_HEIGHT - 20 - textHeight);

                if (newValue.doubleValue() == 0){
                    KeyEventHandler.g.setLayoutY(0);
                } else if (newValue.doubleValue() == 10){
                    KeyEventHandler.g.setLayoutY(bottomLayoutY);
                } else {
                    double currentLayoutY = (bottomLayoutY / 10.0) * newValue.doubleValue();
                    KeyEventHandler.g.setLayoutY(currentLayoutY);
                }

                Editor.textStartY = (int) KeyEventHandler.g.getLayoutY(); // when to update
                KeyEventHandler.render();
        });


        /* Register listeners that resize Allen when the window is re-sized.
           Instead of using anonymous new ChangeListener<Number>(), a stateless lambda object is used because the
           same lambda object can be reused by Java runtime during subsequent invocations*/
        scene.widthProperty().addListener(
                (ObservableValue<? extends Number> observableValue,
                 Number oldScreenWidth,
                 Number newScreenWidth) -> {
                WINDOW_WIDTH = newScreenWidth.intValue();
                usableScreenWidth = WINDOW_WIDTH - scrollBar.getLayoutBounds().getWidth();
                scrollBar.setLayoutX(usableScreenWidth);
                KeyEventHandler.render();

        });

        scene.heightProperty().addListener(
            (ObservableValue<? extends Number> observableValue,
             Number oldScreenHeight,
             Number newScreenHeight) -> {
                WINDOW_HEIGHT = newScreenHeight.intValue();
                scrollBar.setPrefHeight(WINDOW_HEIGHT);
                KeyEventHandler.render();

        });

        /* This is boilerplate, necessary to setup the window where things are displayed. */
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Expected usage: File <filename>");
            System.exit(1);
        }

        launch(args);
    }
}
