package ch.epfl.gameboj.gui;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import ch.epfl.gameboj.GameBoy;
import ch.epfl.gameboj.component.Joypad.Key;
import ch.epfl.gameboj.component.cartridge.Cartridge;
import ch.epfl.gameboj.component.lcd.LcdImage;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
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
    private double simulationSpeed = 1.0;
    private boolean pause;
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
        final List<String> arguments = getParameters().getRaw();
        if (arguments.size() != 1)
            System.exit(1);
        
        final File romFile = new File(arguments.get(0));
        gb = new GameBoy(Cartridge.ofFile(romFile));
        LcdImage li = gb.lcdController().currentImage();

        final ImageView imageView = new ImageView();
        imageView.setImage(ImageConverter.convert(li));
        imageView.setFitWidth(li.width() * 4);
        imageView.setFitHeight(li.height() * 4);
        
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
        
        //Tuiles
        RadioButton tileButton =  new RadioButton("Show Tiles");
        LcdImage tiles = gb.lcdController().tiles();

        ImageView tilesImageView = new ImageView();
        tilesImageView.setImage(ImageConverter.convert(tiles));
        tilesImageView.setFitWidth(tiles.width() * 2);
        tilesImageView.setFitHeight(tiles.height() * 2);
        
        BorderPane tilePane = new BorderPane(tilesImageView);
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
        
        //Vitesse d'émulation
        Label lab = new Label(" Speed : x");
        ChoiceBox<String> cb = new ChoiceBox<String>(FXCollections.observableArrayList(
                "1", "1.5", "2", "5")
                );
        cb.getSelectionModel().selectFirst();;
        cb.setOnAction(e -> {
            simulationSpeed = Double.parseDouble(cb.getValue());
        }
                );

        //Pause
        Button pauseButton = new Button("Pause");
        pauseButton.setMinWidth(100);
        pauseButton.setOnMouseReleased(e -> {
            if (pauseButton.getText().equals("Pause")) {
                pauseButton.setText("Play ");
                pause = true;
            } else {
                pauseButton.setText("Pause");
                pause = false;
            }                
        }
                );

        //Screenshot
        GridPane screenshotPane = new GridPane();
        Label screenshotName = new Label("Name :");
        TextField nameS = new TextField();
        Button saveScreenshotButton = new Button("Save screenshot");
        screenshotPane.addRow(0, screenshotName, nameS);
        screenshotPane.addRow(1, saveScreenshotButton);
        Scene screenshotScene = new Scene(screenshotPane);
        Stage screenshotStage = new Stage();
        screenshotStage.setTitle("SCREENSHOT");

        Button screenshotButton = new Button("Screenshot");
        screenshotButton.setOnMouseReleased(e -> {
            pause = true;
            screenshotStage.setScene(screenshotScene);
            screenshotStage.show();
            saveScreenshotButton.setOnMouseReleased(f -> {  
                try {
                    String name = nameS.getText();
                    if (name.equals(""))
                        name = "screenshot";
                    final Image i = ImageConverter.convert(gb.lcdController().currentImage());
                    final BufferedImage bi = SwingFXUtils.fromFXImage(i, null);
                    ImageIO.write(bi, "png", new File(name  + ".png"));
                    screenshotStage.close();
                    stage.requestFocus();
                    pause = false;
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            });
        });
        screenshotStage.setOnCloseRequest(e -> {pause = false;});

        //Reset
        Button resetButton = new Button("Reset");
        resetButton.setOnMouseReleased(e -> {
            try {
                resetGb();
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        });

        
        
        GridPane buttonsPane = new GridPane();
        buttonsPane.addRow(0, lab, cb, pauseButton, resetButton, screenshotButton, tileButton);
        BorderPane gamePane = new BorderPane(imageView, buttonsPane, null, null, null);

        Scene scene = new Scene(gamePane);
        stage.setScene(scene);
        stage.setTitle("GAMEBOY SIMULATOR");

        stage.show();

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                long elapse = now - before;
                before = now;
                if (!pause)
                    cycle += (long)(GameBoy.NB_CYCLES_P_NANOSECOND * elapse * simulationSpeed);
                gb.runUntil(cycle);
                final LcdImage li = gb.lcdController().currentImage();

                imageView.requestFocus();
                imageView.setImage(ImageConverter.convert(li));

                final LcdImage tiles = gb.lcdController().tiles();
                tilesImageView.setImage(ImageConverter.convert(tiles));
            }
        };
        timer.start();
    }

    private void resetGb() throws Exception {
        List<String> arguments = getParameters().getRaw();
        final File romFile = new File(arguments.get(0));
        gb = new GameBoy(Cartridge.ofFile(romFile));
        cycle = 0;
    }
}



