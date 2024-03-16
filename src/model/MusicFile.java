package model;

import java.io.File;

import application.MusicPlayer;
import javafx.collections.MapChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.util.Duration;

// TODO: Auto-generated Javadoc

/**
 * The Class MusicFile.
 */
public class MusicFile extends HBox {

    /**
     * The file name.
     */
    private String fileName;

    /**
     * The music name.
     */
    private final Text musicName;

    /**
     * The album art.
     */
    private final ImageView albumArt;

    /**
     * The artist.
     */
    private final Text artist;

    /**
     * The music length.
     */
    private final Text musicLength;

    /**
     * The playing logo.
     */
    public ImageView playingLogo;

    /**
     * The music.
     */
    private Media music;

    /**
     * Instantiates a new music file.
     */
    public MusicFile(File location) {
        this.setSpacing(5);
        this.getStylesheets().add(MusicPlayer.STYLESHEET_MUSIC);
        this.getStyleClass().add("music_file");
        albumArt = new ImageView(new Image("/images/ic_headset_black_36dp.png"));
        albumArt.setTranslateY(5);
        albumArt.setFitHeight(30);
        albumArt.setFitWidth(30);

        HBox musicDetailsContainer = new HBox();
        musicDetailsContainer.setSpacing(20);
        musicDetailsContainer.setTranslateY(-5);
        HBox.setHgrow(musicDetailsContainer, Priority.NEVER);
        musicDetailsContainer.setAlignment(Pos.CENTER);
        this.musicName = new Text();
        this.musicName.getStyleClass().add("music-title-detail");

        this.musicName.setText(this.getFileName());

        this.artist = new Text();
        this.artist.getStyleClass().add("music-title-detail");

        this.setArtist(this.getArtist());

        HBox musicLengthContainer = new HBox();
        musicLengthContainer.setSpacing(30);
        musicLengthContainer.setAlignment(Pos.CENTER_RIGHT);
        this.musicLength = new Text();
        this.musicLength.getStyleClass().add("music-title-detail");
        playingLogo = new ImageView("/images/ic_equalizer_black_36dp.png");
        playingLogo.setVisible(false);
        playingLogo.setFitHeight(30);
        playingLogo.setFitWidth(30);

        setMedia(location);

        musicLengthContainer.getChildren().addAll(playingLogo, this.musicLength);
        musicDetailsContainer.getChildren().addAll(this.musicName, this.artist, musicLengthContainer);

        this.getChildren().addAll(/*albumArt, */musicDetailsContainer);
        this.setPadding(new Insets(9));

    }

    /**
     * Sets the music length.
     *
     * @param i the Duration
     * @return the formatted duration
     */
    public String setMusicLength(Duration i) {
        String time = "";

        if (i.greaterThan(Duration.ZERO)) {

            int intDuration = (int) Math.floor(i.toSeconds());

            int durationHours = intDuration / (60 * 60);

            if (durationHours > 0) {

                intDuration -= durationHours * 60 * 60;

            }

            int durationMinutes = intDuration / 60;

            int durationSeconds = intDuration - durationHours * 60 * 60 - durationMinutes * 60;

            if (durationHours > 0) {

                time = String.format("%d:%02d:%02d", durationHours, durationMinutes, durationSeconds);

            } else {
                time = String.format("%02d:%02d", durationMinutes, durationSeconds);
            }

        }
        return time;

    }

    /**
     * Gets the file name.
     *
     * @return the file name
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Sets the media.
     *
     * @param location the new media
     */
    private void setMedia(File location) {
        try {

            music = new Media(location.toURI().toURL().toString());
            music.getMetadata().addListener((MapChangeListener<String, Object>) ch -> {
                if (ch.wasAdded()) {
                    handleMetadata(ch.getKey(), ch.getValueAdded());
                }
            });
            final MediaPlayer player = new MediaPlayer(music);
            player.setOnReady(() -> {
                Duration duration = player.getMedia().getDuration();
                this.musicLength.setText(this.setMusicLength(duration));

            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Handle metadata.
     *
     * @param key   the key
     * @param value the value
     */
    private void handleMetadata(String key, Object value) {
        if (key.equals("album")) {
            // album.setText(value.toString());
            // System.out.println(value.toString());

        } else if (key.equals("artist")) {
            // artist.setText(value.toString());
            StringBuilder artistLimit = new StringBuilder(value.toString());
            artistLimit.setLength(20);
            this.setArtist(artistLimit.toString());
        }
        if (key.equals("title")) {
            StringBuilder artistLimit = new StringBuilder(value.toString());
            artistLimit.setLength(20);
            this.setFileName(artistLimit.toString());
        }
        if (key.equals("year")) {
            // year.setText(value.toString());
            // System.out.println(value.toString());
        }
        if (key.equals("image")) {

            if (value == null) {
                albumArt.setImage(new Image("/images/ic_headset_black_36dp.png"));
            } else {
                albumArt.setImage((Image) value);
            }
            /*
             * try { albumArt.setImage((Image) value); } catch (Exception e) {
             * albumArt.setImage(new
             * Image("/images/ic_headset_white_36dp.png")); }
             */

        }
    }

    /**
     * Gets the album art.
     *
     * @return the album art
     */
    public Image getAlbumArt() {
        return albumArt.getImage();
    }

    /**
     * Sets the file name.
     *
     * @param fileName the new file name
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
        this.musicName.setText(this.getFileName());
    }

    /**
     * Gets the file path.
     *
     * @return the file path
     */
    public String getFilePath() {
        return this.music.getSource();
    }

    /**
     * Gets the artist.
     *
     * @return the artist
     */
    public String getArtist() {
        return artist.getText();
    }

    /**
     * Sets the artist.
     *
     * @param artist the new artist
     */
    public void setArtist(String artist) {
        this.artist.setText(artist);
    }

}
