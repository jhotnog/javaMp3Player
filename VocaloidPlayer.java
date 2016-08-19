/**
 * JavaFx MP3 Player
 * Takes in song names from command line
 * and plays them for you.
 * Has basic music player UI controls.
 * Next Feature: Will auto-generate songs from
 * resource folder instead of typing in at shell.
 * @param List<String>
 * @version 1.1 Added shuffle and skip functions
 * @author Jeff Hotnog
 */
import java.io.*;
import java.net.URL;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
//import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.Node;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;


public class VocaloidPlayer extends Application {
    static int order;
    static int backgroundNum;
	@Override public void start(Stage stage) throws Exception {
        Random rand = new Random();
        order = 0;
        backgroundNum = 0;

        // Previously used for command line usage
        //List<String> songs = this.getParameters().getRaw();
        List<String> songs = new ArrayList<>();

        String homeDir = System.getProperty("user.home");

        // Path variable needs to be changed before running program
        // based on user's file directory structure.
        String musicPath = homeDir + "/Documents/Java Programs/javaMp3Player/Resources/";
        File folder = new File(musicPath);
        File[] listOfFiles = folder.listFiles();

        for(int i = 0; i < listOfFiles.length; i++) {
            if(listOfFiles[i].isFile()) {
                String fileName = listOfFiles[i].getName();
                songs.add(fileName);
            }
        }

        List<URL> songList = new ArrayList<>();
        for (int i = 0; i < songs.size(); i++) {
            String song = "/Resources/" + songs.get(i);
            URL resource = getClass().getResource(song);
            songList.add(resource);
        }

        Media[] media = new Media[songList.size()];
        for (int i = 0; i < songList.size(); i++) {
            media[i] = new Media(songList.get(i).toString());
        }

        MediaPlayer[] playerArray = new MediaPlayer[media.length];
        for (int i = 0; i < playerArray.length; i++) {
            playerArray[i] = new MediaPlayer(media[i]);
        }

        int playerLength = playerArray.length - 1;

        Group layout = new Group();

        String imagePath = homeDir + "/Documents/Java Programs/javaMp3Player/Resources/Backgrounds/";
        File imageFolder = new File(imagePath);
        File[] listOfImageFiles = imageFolder.listFiles();
        Image[] backgrounds = new Image[listOfImageFiles.length];

        for(int i = 0; i < listOfImageFiles.length; i++) {
            if(listOfImageFiles[i].isFile()) {
                backgrounds[i] = new Image(listOfImageFiles[i].toURI().toString(), 480,380,false,false);
            }
        }

		//Image backgroundImage = new Image("/Resources/Backgrounds/background.png", 420, 0, false, false);
        // Image needs to be in same directory as src file and one
        // can use any image, just needs to be named same as string parameter
        // above
		ImageView iv1 = new ImageView();
		iv1.setImage(backgrounds[backgroundNum]);

        layout.getChildren().add(iv1);
        Scene scene = new Scene(layout);
		//scene.setFill(Color.CYAN);

		/*TextField inputField = new TextField();
        //String songName = inputField.getText(); // File name of a song with *.mp3
        */

		Button playButton = new Button();
		playButton.setText("Play");

		Button pauseButton = new Button();
		pauseButton.setText("Pause");

		Button stopButton = new Button();
		stopButton.setText("Stop");

        Button prevSong = new Button();
        prevSong.setText("<<");

        Button nextSong = new Button();
        nextSong.setText(">>");

        Button shuffle = new Button();
        shuffle.setText("Shuffle");

        ToggleButton repeat = new ToggleButton("Repeat");

        ToggleButton repeatAll = new ToggleButton("Repeat All");

        Button changeBackground = new Button("Background");

        if (playerArray.length == 0) {
            playButton.setDisable(true);
            pauseButton.setDisable(true);
            stopButton.setDisable(true);
            prevSong.setDisable(true);
            nextSong.setDisable(true);
            shuffle.setDisable(true);
        }

        else if (playerArray.length == 1) {
            shuffle.setDisable(true);
            prevSong.setDisable(true);
            nextSong.setDisable(true);
        }

        if(backgrounds.length < 2) {
            changeBackground.setDisable(true);
        }

        playButton.setOnAction(e -> {
            stage.setTitle(songs.get(order));
            playerArray[order].setOnEndOfMedia(new Runnable() {
                public void run() {
                    playerArray[order].stop();
                    if (order == playerArray.length - 1) {
                        playerArray[order].stop();
                        playerArray[0].play();
                    }
                    stage.setTitle(songs.get(order));
                    playerArray[order + 1].play();
                }
            });
            playerArray[order].play();
        });

        pauseButton.setOnAction(e -> {
			playerArray[order].pause();
		});

		stopButton.setOnAction(e -> {
            stage.setTitle("Welcome to Vocaloid Player (^_^)");
            playerArray[order].stop();
            order = 0;
		});

        prevSong.setOnAction(e -> {
            playerArray[order].stop();
            if (order > 0) {
                order--;
            }
            else if (order == 0) {
                order = playerArray.length - 1;
            }
            stage.setTitle(songs.get(order));
            playerArray[order].play();
        });

        nextSong.setOnAction(e -> {
            playerArray[order].stop();
            if (order < playerArray.length - 1) {
                order++;
            }
            else if (order == playerArray.length - 1) {
                order = 0;
            }
            stage.setTitle(songs.get(order));
            playerArray[order].play();
        });

        shuffle.setOnAction(e -> {
            playerArray[order].stop();
            int value = rand.nextInt(songs.size());
            while (value == order) {
                value = rand.nextInt(songs.size());
            }
            order = value;
            stage.setTitle(songs.get(value));
            playerArray[order].play();
        });

        repeat.setOnAction(e -> {
            if (repeat.isSelected()) {
                playerArray[order].setOnEndOfMedia(new Runnable() {
                    public void run() {
                        playerArray[order].seek(Duration.ZERO);
                    }
                });
            } else if (!(repeat.isSelected())) {
                playerArray[order].setOnEndOfMedia(null);
            }
        });

        repeatAll.setOnAction(e -> {
            if (repeatAll.isSelected()) {
                playerArray[playerLength].setOnEndOfMedia(new Runnable() {
                    public void run() {
                        playerArray[playerLength].stop();
                        playerArray[0].play();
                    }
                });
            } else if (!(repeatAll.isSelected())) {
                playerArray[playerLength].setOnEndOfMedia(null);
            }
        });

        changeBackground.setOnAction(e -> {
            if(backgroundNum < backgrounds.length - 1) {
                backgroundNum++;
            }
            else if(backgroundNum == backgrounds.length - 1) {
                backgroundNum = 0;
            }
            iv1.setImage(backgrounds[backgroundNum]);
        });

		HBox entryBox = new HBox();
		//entryBox.getChildren().add(inputField);
		entryBox.getChildren().add(playButton);
		//entryBox.getChildren().add(pauseButton);
		entryBox.getChildren().add(stopButton);
		entryBox.getChildren().add(prevSong);
        entryBox.getChildren().add(nextSong);
        entryBox.getChildren().add(repeat);
        entryBox.getChildren().add(repeatAll);
        entryBox.getChildren().add(shuffle);
        entryBox.getChildren().add(changeBackground);
		layout.getChildren().add(entryBox);

		stage.setWidth(480);
		stage.setHeight(380);
        stage.setResizable(false);
        // Window can be resized by changing above values
		stage.setScene(scene);
		stage.setTitle("Welcome to Vocaloid Player (^_^)");
		//stage.sizeToScene();
		stage.show();
	}
}
