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
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {
    
    private static Map<Object, Key> keyMap = Map.of(    "a", Key.A,
                                                        "b", Key.B,
                                                        "s", Key.START,
                                                        KeyCode.SPACE, Key.SELECT,
                                                        KeyCode.UP, Key.UP,
                                                        KeyCode.DOWN, Key.DOWN,
                                                        KeyCode.LEFT, Key.LEFT,
                                                        KeyCode.RIGHT, Key.RIGHT
                                                        );

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
        imageView.setFitWidth(li.width() * 4);
        imageView.setFitHeight(li.height() * 4);
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
        
        BorderPane border = new BorderPane(imageView);
        Scene scene = new Scene(border);
        stage.setScene(scene);

        stage.show();
        imageView.requestFocus();
        
        long start = System.nanoTime();
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                long elapsed = now - start;
                long cycle = (long)(elapsed * GameBoy.NB_CYCLES_P_NANOSECOND);
                gb.runUntil(cycle);
                LcdImage li = gb.lcdController().currentImage();
                imageView.setImage(ImageConverter.convert(li));
            }
        };
        timer.start();
    }
}

