package com.example.classicalcomposers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class HelloController implements Initializable {
    //setting up our variables
    @FXML
    private Label composerQuote, songNameLabel;
    @FXML
    private Button playButton, pauseButton, resetButton;
    @FXML
    private ComboBox composerSelectComboBox;
    @FXML
    private ImageView composerImage;
    @FXML
    private TextArea composerNameAndDates;
    @FXML
    private TextArea composerDescription;
    @FXML
    private Slider volumeSlider;
    @FXML
    private ProgressBar progressBar;
    private String composerList[] = {"Bach","Beethoven","Chopin","Debussy","Erik Satie","Mozart","Schubert","Strauss","Tchaikovsky","Vivaldi"};
    private Media media;
    private MediaPlayer mediaPlayer;
    private File directory;
    private File[] files;
    private ArrayList<File> songs;
    private int songNumber;
    private Timer timer;
    private TimerTask task;
    private boolean running;

    //initializing our variables
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //loading all the songs
        songs = new ArrayList<File>();
        directory = new File("music");
        files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                songs.add(file);
            }
        }
        media = new Media(songs.get(songNumber).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        songNameLabel.setText(songs.get(songNumber).getName());

        //when slider changes, set the volume accordingly
        volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
                mediaPlayer.setVolume(volumeSlider.getValue() * 0.01);
            }
        });

        progressBar.setStyle("-fx-accent: #00FF00;");

        //setting default selection in combo box
        composerSelectComboBox.getItems().addAll(composerList);
        composerSelectComboBox.getSelectionModel().select(composerList[0]);

        //load all the other information about the currently selected composer by calling changeComposer method
        try {
            changeComposer(null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    //play media method starts the timer(which is used in progressBar) and plays the media with the designated volume
    public void playMedia() {
        beginTimer();
        mediaPlayer.setVolume(volumeSlider.getValue() * 0.01);
        mediaPlayer.play();
    }
    //pause media method stops the timer and pauses the media player
    public void pauseMedia() {
        cancelTimer();
        mediaPlayer.pause();
    }
    //reset media sets the progress of the progress bar to 0 and the media player starts from the beginning
    public void resetMedia() {
        progressBar.setProgress(0);
        mediaPlayer.seek(Duration.seconds(0));
    }

    //this is the begin timer method that is being used to display the progress of the song using the progress bar
    public void beginTimer() {
        timer = new Timer();
        task = new TimerTask() {
            public void run() {
                running = true;
                double current = mediaPlayer.getCurrentTime().toSeconds();
                double end = media.getDuration().toSeconds();
                progressBar.setProgress(current/end);
                if(current/end == 1) {
                    cancelTimer();
                }
            }
        };
        //this is how often the progress bar updates
        timer.scheduleAtFixedRate(task, 0, 1000);
    }
    //this stops the timer
    public void cancelTimer() {
        running = false;
        timer.cancel();
    }

    //change composer method triggers when the user picks another composer using the combo box.
    //if the user selects the first composer, then all the files needed for that composer will be loaded and shown
    public void changeComposer(ActionEvent event) throws IOException {

        int i = composerSelectComboBox.getSelectionModel().getSelectedIndex();

        if(composerSelectComboBox.getSelectionModel().getSelectedIndex() == i){

            //stop the current song and load the new one
            mediaPlayer.dispose();
            media = new Media(songs.get(i).toURI().toString());//media = new Media.get(INDEX).konvertuj to u pathname i konvertuj to u string))
            mediaPlayer = new MediaPlayer(media);//ovo je plejer koji pusta muziku tj plejer koju pusta mediu koju smo kreirali gore
            songNameLabel.setText(songs.get(i).getName());//label text = arraylist songs.get(index).getname
            playMedia();

            //change the short description
            File shortDescFilePath = new File(String.valueOf("shortDesc/" + i + ".txt"));
            String text1 = new String(Files.readAllBytes(Paths.get(shortDescFilePath.toURI())));
            composerNameAndDates.setText(text1);

            //change description
            File descFilePath = new File(String.valueOf("desc/" + i + ".txt"));
            String text2 = new String(Files.readAllBytes(Paths.get(descFilePath.toURI())));
            composerDescription.setText(text2);

            //change the picture
            InputStream stream = new FileInputStream("img/" + i + ".jpg");
            Image image = new Image(stream);
            composerImage.setImage(image);

            //change the quote
            File quoteFilePath = new File(String.valueOf("quote/" + i + ".txt"));
            String text3 = new String(Files.readAllBytes(Paths.get(quoteFilePath.toURI())));
            composerQuote.setText(text3);
        }

    }
}