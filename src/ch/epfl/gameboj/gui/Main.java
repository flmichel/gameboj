package ch.epfl.gameboj.gui;

import java.io.File;
import java.util.List;
import java.util.Map;

import ch.epfl.gameboj.GameBoy;
import ch.epfl.gameboj.component.Joypad.Key;
import ch.epfl.gameboj.component.cartridge.Cartridge;
import ch.epfl.gameboj.component.lcd.LcdImage;
import ch.epfl.gameboj.component.memory.Ram;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Contient le programme principal du simulateur. 
 * @author Riand Andre
 * @author Michel François
 */
public class Main extends Application {

    private static Map<Object, Key> keyMap = Map.of(    
            "a", Key.A,
            "b", Key.B,
            "s", Key.START,
            KeyCode.SPACE, Key.SELECT,
            KeyCode.UP, Key.UP,
            KeyCode.DOWN, Key.DOWN,
            KeyCode.LEFT, Key.LEFT,
            KeyCode.RIGHT, Key.RIGHT
            );
    private double simulationSpeed = 1.0;
    private long cycle = 0;
    private long before = System.nanoTime();
    private GameBoy gb;

    /**
     * Lance l'application
     * @param args : parametres de lancement
     */
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        List<String> arguments = getParameters().getRaw();
        if (arguments.size() != 1)
            System.exit(1);
        File romFile = new File(arguments.get(0));
        gb = new GameBoy(Cartridge.ofFile(romFile));
        LcdImage li = gb.lcdController().currentImage();

        ImageView imageView = new ImageView();
        imageView.setImage(ImageConverter.convert(li));
        imageView.setFitWidth(li.width() * 4);
        imageView.setFitHeight(li.height() * 4);

        LcdImage sprites = gb.lcdController().spriteTiles();

        ImageView spriteImageView = new ImageView();
        spriteImageView.setImage(ImageConverter.convert(sprites));
        spriteImageView.setFitWidth(sprites.width() * 2);
        spriteImageView.setFitHeight(sprites.height() * 2);

        Label lab = new Label(" Speed : x");
        ChoiceBox<String> cb = new ChoiceBox<String>(FXCollections.observableArrayList(
                "1", "1.5", "2", "5")
                );
        cb.getSelectionModel().selectFirst();;
        cb.setOnAction(e -> {
            simulationSpeed = Double.parseDouble(cb.getValue()); //prof a dit que ct bien que la speed soit associee au nombre
        }
                );



        imageView.setOnKeyPressed(e -> {
            if (keyMap.containsKey(e.getCode())) {
                gb.joypad().keyPressed(keyMap.get(e.getCode()));
            } else if (keyMap.containsKey(e.getText())) {
                gb.joypad().keyPressed(keyMap.get(e.getText()));
            }
        });
        imageView.setOnKeyReleased(e -> {
            if (keyMap.containsKey(e.getCode())) {
                gb.joypad().keyReleased(keyMap.get(e.getCode()));
            } else if (keyMap.containsKey(e.getText())) {
                gb.joypad().keyReleased(keyMap.get(e.getText()));
            }
        });


        RadioButton tileButton =  new RadioButton("Show Tiles");

        Button resetButton = new Button("Reset");
        resetButton.setOnMouseReleased(e -> {
            try {
                resetGb();
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        });

        Button saveButton = new Button("Save game");

        Label saveLabel = new Label ("Game saved");
        BorderPane messagePane = new BorderPane(null);

        GridPane buttonsPane = new GridPane();
        buttonsPane.addRow(0, lab, cb, resetButton, saveButton, tileButton);
        BorderPane gamePane = new BorderPane(imageView, buttonsPane, null, null, null);

        BorderPane tilePane = new BorderPane(spriteImageView);
        Scene tileScene = new Scene(tilePane);
        Stage tileStage = new Stage();
        tileStage.setTitle("TILES");

        tileButton.setOnMouseReleased(e -> {
            if (tileButton.isSelected()) {

                tileStage.setScene(tileScene);
                tileStage.setX(40);
                tileStage.show();
                stage.requestFocus();

            } else {

                tileStage.close();
            }
        });

        saveButton.setOnMouseClicked(e -> {           
            messagePane.setCenter(new Label ("Game saved"));
        });    

        Scene scene = new Scene(gamePane);
        stage.setScene(scene);
        stage.setTitle("GAMEBOY SIMULATOR");

        stage.show();

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                long elapse = now - before;
                before = now;
                cycle += (long)(GameBoy.NB_CYCLES_P_NANOSECOND * elapse * simulationSpeed);
                gb.runUntil(cycle);
                LcdImage li = gb.lcdController().currentImage();

                imageView.requestFocus();
                imageView.setImage(ImageConverter.convert(li));

                LcdImage sprites = gb.lcdController().spriteTiles();
                spriteImageView.setImage(ImageConverter.convert(sprites));
            }
        };
        timer.start();
    }

    private void resetGb() throws Exception {
        List<String> arguments = getParameters().getRaw();
        File romFile = new File(arguments.get(0));
        gb = new GameBoy(Cartridge.ofFile(romFile));
        cycle = 0;
    }
}



