package ch.epfl.gameboj.gui;

import java.io.File;
import java.util.List;
import java.util.Map;

import ch.epfl.gameboj.GameBoy;
import ch.epfl.gameboj.component.Joypad.Key;
import ch.epfl.gameboj.component.cartridge.Cartridge;
import ch.epfl.gameboj.component.lcd.LcdImage;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
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
        GameBoy gb = new GameBoy(Cartridge.ofFile(romFile));
        LcdImage li = gb.lcdController().currentImage();
        
        ImageView imageView = new ImageView();
        imageView.setImage(ImageConverter.convert(li));
        imageView.setFitWidth(li.width() * 2);
        imageView.setFitHeight(li.height() * 2);
        
        LcdImage sprites = gb.lcdController().spriteTiles();

        ImageView spriteImageView = new ImageView();
        spriteImageView.setImage(ImageConverter.convert(sprites));
        spriteImageView.setFitWidth(sprites.width() * 2);
        spriteImageView.setFitHeight(sprites.height() * 2);
        
       
        Button speedButton = new Button("x1");
        speedButton.setOnMouseReleased(e -> {
            switch (speedButton.getText()) {    
            case "x1" : {
                simulationSpeed += 0.5;
                speedButton.setText("x1.5");
            } break;
            case "x1.5" : {  
                simulationSpeed += 0.5;
                speedButton.setText("x2");
            } break;
            case "x2" : {
                simulationSpeed = 5;
                speedButton.setText("x5");
            } break;
            case "x5" : {
                simulationSpeed = 1;
                speedButton.setText("x1");
            } break;
            }
        }
                );
        
        Button visButton = new Button("Hide tiles");
        
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

        BorderPane pane = new BorderPane(imageView, speedButton, spriteImageView, null, visButton);
        
        visButton.setOnMouseReleased(e -> {
            if (visButton.getText() == "Show tiles") {
                visButton.setText("Hide tiles");
                pane.setRight(spriteImageView);
            } else {
                visButton.setText("Show tiles");
                pane.setRight(null);
            }
        });
        
        Scene scene = new Scene(pane);
        stage.setScene(scene);

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
}

