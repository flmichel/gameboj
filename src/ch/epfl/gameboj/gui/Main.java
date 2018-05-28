package ch.epfl.gameboj.gui;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    private static final Map<Object, Key> keyMap = Map.of(    
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
    boolean pause;
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
            final Image i = ImageConverter.convert(gb.lcdController().currentImage());
            final BufferedImage bi = SwingFXUtils.fromFXImage(i, null);
            saveScreenshotButton.setOnMouseReleased(f -> {  
                try {
                    String name = nameS.getText();
                    if (name.equals(""))
                        name = "screenshot";
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

        Button resetButton = new Button("Reset");
        resetButton.setOnMouseReleased(e -> {
            try {
                resetGb();
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        });

//        Button saveButton = new Button("Save game");
//        
//        Button loadButton = new Button("Load");

//        Label saveLabel = new Label ("Game saved");
//        BorderPane messagePane = new BorderPane(saveLabel);

        GridPane buttonsPane = new GridPane();
        buttonsPane.addRow(0, lab, cb, pauseButton, resetButton, screenshotButton, tileButton);
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

        
//        Button okButton = new Button("Ok");
//        messagePane.setRight(okButton);
//        
//        okButton.setOnMouseReleased(f -> {
//            gamePane.setBottom(null); 
//            stage.setHeight(stage.getHeight()-30);
//        });
             
//        saveButton.setOnMouseClicked(e -> { 
//            saveGame(gb);
//            gamePane.setBottom(messagePane); 
//            stage.setHeight(stage.getHeight()+30);
//        });    
//        
//        loadButton.setOnMouseClicked(e -> { 
//          loadGame("SAUVEGARDE.bin") ; 
//        });

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
    
//    private void saveGame(GameBoy gb) {
//        FileOutputStream fop = null;
//        File file;
//
//        try {
//
//            file = new File("SAUVEGARDE.bin");
//            fop = new FileOutputStream(file);
//
//            // if file doesnt exists, then create it
//            if (!file.exists()) {
//                file.createNewFile();
//            }
//
//            // get the content in bytes
//            byte[] content = new byte[gb.ram().size()];
//            for (int i=0 ; i< content.length ; i++) {
//                content[i] = (byte) gb.ram().read(i);
//            }
//            
//            fop.write(content);
//            fop.flush();
//            fop.close();
//
//            System.out.println("Done");
//            
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (fop != null) {
//                    fop.close();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }       
//        }
//    }
//    
//    private void loadGame(String fileName) {
//     
//        BufferedReader br = null;
//        FileReader fr = null;
//
//        try {
//
//            //br = new BufferedReader(new FileReader(FILENAME));
//            fr = new FileReader(fileName);
//            br = new BufferedReader(fr);
//
//            for(int i = 0 ; i < gb.ram().size() ; i++) {
//                gb.ram().write(i, br.read());
//                }
//
//        } catch (IOException e) {
//
//            e.printStackTrace();
//
//        } finally {
//
//            try {
//
//                if (br != null)
//                    br.close();
//
//                if (fr != null)
//                    fr.close();
//
//            } catch (IOException ex) {
//
//                ex.printStackTrace();
//
//            }
//
//        }
//    }
}



