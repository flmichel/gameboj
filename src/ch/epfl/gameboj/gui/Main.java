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

/**
 * Contient le programme principal du simulateur. 
 * @author Riand Andre
 * @author Michel François
 */
public class Main extends Application {

    private static final Map<String, Key> keyMapString = Map.of(    
            "a", Key.A,
            "b", Key.B,
            "s", Key.START
            );
    private static final Map<KeyCode, Key> keyMapCode = Map.of(    
            KeyCode.SPACE, Key.SELECT,
            KeyCode.UP, Key.UP,
            KeyCode.DOWN, Key.DOWN,
            KeyCode.LEFT, Key.LEFT,
            KeyCode.RIGHT, Key.RIGHT
            );

    /**
     * Lance l'application
     * @param args : parametres de lancement
     */
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        final List<String> arguments = getParameters().getRaw();
        if (arguments.size() != 1)
            System.exit(1);
        final File romFile = new File(arguments.get(0));
        final GameBoy gb = new GameBoy(Cartridge.ofFile(romFile));
        final LcdImage li = gb.lcdController().currentImage();

        ImageView imageView = new ImageView();
        imageView.setImage(ImageConverter.convert(li));
        imageView.setFitWidth(li.width() * 2);
        imageView.setFitHeight(li.height() * 2);
        imageView.setOnKeyPressed(e -> {
            if (keyMapCode.containsKey(e.getCode())) {
                gb.joypad().keyPressed(keyMapCode.get(e.getCode()));
            } else if (keyMapString.containsKey(e.getText())) {
                gb.joypad().keyPressed(keyMapString.get(e.getText()));
            }
        });
        imageView.setOnKeyReleased(e -> {
            if (keyMapCode.containsKey(e.getCode())) {
                gb.joypad().keyReleased(keyMapCode.get(e.getCode()));
            } else if (keyMapString.containsKey(e.getText())) {
                gb.joypad().keyReleased(keyMapString.get(e.getText()));
            }
        });

        final BorderPane border = new BorderPane(imageView);
        final Scene scene = new Scene(border);
        stage.setScene(scene);

        stage.show();
        imageView.requestFocus();

        final long start = System.nanoTime();
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                final long elapsed = now - start;
                final long cycle = (long)(elapsed * GameBoy.NB_CYCLES_P_NANOSECOND);
                gb.runUntil(cycle);
                final LcdImage li = gb.lcdController().currentImage();
                imageView.setImage(ImageConverter.convert(li));
            }
        };
        timer.start();
    }
}

