package decoder;

import java.net.URL;
import java.util.Random;
import java.util.Stack;

import application.MusicLayout;
import component.MiniControl;
import component.Toast;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.value.ObservableValue;
import javafx.collections.MapChangeListener;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import model.PlayType;

/**
 * The Class CommonCodec.
 */
@SuppressWarnings("unused")
public class CommonCodec extends HBox {

    /**
     * The player.
     */
    public MediaPlayer player;

    /**
     * The song file.
     */
    private Media songFile;

    /**
     * The duration.
     */
    private Duration duration;

    /**
     * The status.
     */
    private MediaPlayer.Status status;

    /**
     * The repeat.
     */
    private final boolean repeat = false;

    /**
     * The stop requested.
     */
    private boolean stopRequested = false;

    /**
     * The at end of media.
     */
    private boolean atEndOfMedia = false;

    private boolean isFinishedShuffles;

    /**
     * The play time.
     */
    private final Label playTime;

    /**
     * The time slider.
     */
    private final Slider volumeSlider;
    private final Slider timeSlider;

    /**
     * The currently used volume.
     */
    double currentlyUsedVolume;

    /**
     * The play button.
     */
    public MiniControl playButton;

    /**
     * The volume controls.
     */
    public HBox volumeControls;

    /**
     * The mock.
     */
    public MiniControl miniControl;

    /**
     * The man.
     */
    MusicLayout musicLayout;

    /**
     * The album art.
     */
    public ImageView albumArt;

    /**
     * The title.
     */
    private String artist, title;

    /**
     * The threshold.
     */
    int threshold = 0;

    int currentTrack = 0;

    public Stack<Integer> playedSongs;

    /**
     * The currently_playing.
     */
    String currentlyPlaying;

    private PlayType playType;

    /**
     * Instantiates a new common codec.
     *
     * @param superman the superman
     */
    public CommonCodec(MusicLayout superman) {

        playedSongs = new Stack<>();
        this.setFinishedShuffles(true);

        musicLayout = superman;
        this.setTranslateY(12);
        this.setTranslateX(15);

        albumArt = new ImageView(new Image("/images/ic_headset_white_36dp.png"));
        albumArt.setFitHeight(30);
        albumArt.setFitWidth(30);

        playButton = new MiniControl("/images/ic_play_arrow_black_48dp.png");

        // initializing the play type
        playType = PlayType.NORMAL;

        playTime = new Label();

        timeSlider = new Slider();
        timeSlider.setMinWidth(190);

        timeSlider.setMaxWidth(Double.MAX_VALUE);

        volumeSlider = new Slider();
        currentlyUsedVolume = 0.0;
        volumeSlider.setMinWidth(90);

        HBox otherControls = new HBox();
        otherControls.setTranslateY(9);

        volumeControls = new HBox();
        volumeControls.setVisible(false);
        volumeControls.setTranslateY(-1);
        volumeControls.setTranslateX(80);
        ImageView volumeIcon = new ImageView(new Image("/images/ic_volume_down_black_36dp.png"));
        volumeIcon.setFitHeight(25);
        volumeIcon.setFitWidth(25);
        miniControl = new MiniControl("/images/ic_fullscreen_black_36dp.png");
        miniControl.setVisible(false);
        miniControl.setTranslateY(-6);

        volumeControls.getChildren().addAll(volumeIcon, volumeSlider, miniControl);

        otherControls.getChildren().addAll(timeSlider, playTime, volumeControls);

        this.getChildren().addAll(albumArt, playButton, otherControls);
        initialiseFirstSong();
    }

    /**
     * Initialise first song.
     */
    public void initialiseFirstSong() {

        try {
            URL urler = CommonCodec.class.getResource("/raw/test.mp3");
            String url = urler.toURI().toURL().toString();
            this.loadFile(url);
            this.player.setVolume(0.3f);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            // this.player.dispose();
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
            setArtist(value.toString());

        }
        if (key.equals("title")) {
            // title.setText(value.toString());
            // System.out.println(value.toString());
            setTitle(value.toString());
        }
        if (key.equals("year")) {
            // year.setText(value.toString());
            // System.out.println(value.toString());
        }
        if (key.equals("image")) {
            if (albumArt.getImage() == null) {
                albumArt.setImage(new Image("/images/ic_volume_down_black_36dp.png"));
            } else {
                albumArt.setImage((Image) value);
            }

        }
    }

    /**
     * Gets the artist.
     *
     * @return the artist
     */
    public String getArtist() {
        return artist;
    }

    /**
     * Sets the artist.
     *
     * @param artist the new artist
     */
    public void setArtist(String artist) {
        this.artist = artist;
    }

    /**
     * Gets the title.
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title.
     *
     * @param title the new title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Sets the initiate controls.
     *
     * @param superman the new initiate controls
     */
    public void setInitiateControls(MusicLayout superman) {

        this.songFile.getMetadata().addListener((MapChangeListener<String, Object>) ch -> {
            if (ch.wasAdded()) {
                handleMetadata(ch.getKey(), ch.getValueAdded());
            }
        });

        this.player.setOnReady(() -> {

            duration = this.player.getMedia().getDuration();

            updateValues();

        });

        playButton.setOnAction(e -> {
            updateValues();

            try {
                status = this.player.getStatus();
                if (status == MediaPlayer.Status.UNKNOWN || status == MediaPlayer.Status.HALTED) {

                    // don't do anything in these states

                    return;

                } else if (status == MediaPlayer.Status.PAUSED

                        || status == MediaPlayer.Status.READY) {

                    // rewind the movie if we're sitting at the end

                    if (atEndOfMedia) {
                        this.player.seek(this.player.getStartTime());

                        atEndOfMedia = false;

                        playButton.setGraphics("/images/ic_play_arrow_black_48dp.png");

                        updateValues();

                    }

                    this.player.play();
                    volumeControls.setVisible(true);
                    miniControl.setVisible(true);
                    playButton.setGraphics("/images/ic_pause_black_48dp.png");
                    musicLayout.playAll.setGraphics("/images/ic_pause_black_48dp.png");
                } else if (status == MediaPlayer.Status.STOPPED) {
                    this.loadFile(this.player.getMedia().getSource());
                    updateValues();
                    this.player.play();
                    playButton.setGraphics("/images/ic_pause_black_48dp.png");

                } else {
                    this.player.pause();
                    volumeControls.setVisible(false);
                    miniControl.setVisible(false);
                }
            } catch (Exception e1) {
                Alert makeError = new Alert(AlertType.ERROR);
                makeError.setContentText("No File in Media Tray");
                makeError.setTitle("No Media File");
                makeError.showAndWait();

            }
        });

        this.player.setOnEndOfMedia(() -> {

            switch (playType) {
                case NORMAL:
                    if (musicLayout.musicView.getItems().size() > 0) {
                        if (threshold == musicLayout.musicView.getItems().size()) {

                            this.stop();
                            playButton.setGraphics("/images/ic_play_arrow_black_48dp.png");
                            musicLayout.playAll.setGraphics("/images/ic_play_arrow_black_48dp.png");
                            status = MediaPlayer.Status.STOPPED;
                            this.timeSlider.setValue(0);
                            stopRequested = true;
                            atEndOfMedia = true;
                            musicLayout.updateMusicDetails();
                            setInitiateControls(musicLayout);

                        } else {
                            musicLayout.next();
                            threshold += 1;
                        }

                    } else {
                        this.stop();
                        playButton.setGraphics("/images/ic_play_arrow_black_48dp.png");
                        musicLayout.playAll.setGraphics("/images/ic_play_arrow_black_48dp.png");
                        status = MediaPlayer.Status.STOPPED;
                        this.timeSlider.setValue(0);
                        stopRequested = true;
                        atEndOfMedia = true;
                        setInitiateControls(musicLayout);

                    }

                    break;
                case SHUFFLE:

                    if (!this.isFinishedShuffles()) {
                        musicLayout.nextShuffle(loadShuffledSong());
                    } else {
                        this.stop();
                        playButton.setGraphics("/images/ic_play_arrow_black_48dp.png");
                        musicLayout.playAll.setGraphics("/images/ic_play_arrow_black_48dp.png");
                        status = MediaPlayer.Status.STOPPED;
                        this.timeSlider.setValue(0);
                        stopRequested = true;
                        atEndOfMedia = true;
                        setInitiateControls(musicLayout);
                    }
                    break;

            }
        });
        this.player.setOnPlaying(() -> {

            if (stopRequested) {

                this.player.pause();

                stopRequested = false;

            } else {

                playButton.setGraphics("/images/ic_pause_black_48dp.png");
                musicLayout.playAll.setGraphics("/images/ic_pause_black_48dp.png");
            }

        });

        this.player.setOnPaused(() -> {

            playButton.setGraphics("/images/ic_play_arrow_black_48dp.png");
            musicLayout.playAll.setGraphics("/images/ic_play_arrow_black_48dp.png");

        });
        this.player.setOnStopped(() -> {
            playButton.setGraphics("/images/ic_play_arrow_black_48dp.png");
            musicLayout.playAll.setGraphics("/images/ic_play_arrow_black_48dp.png");

        });

        this.player.setOnReady(() -> {

            duration = this.player.getMedia().getDuration();

            updateValues();

        });

        volumeSlider.valueProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {

            if (volumeSlider.isValueChanging()) {
                currentlyUsedVolume = volumeSlider.getValue() / 100.0;

                this.player.setVolume(volumeSlider.getValue() / 100.0);

            }
        });

        this.player.currentTimeProperty().addListener((ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) -> updateValues());

        timeSlider.valueProperty().addListener((Observable ov) -> {

            if (timeSlider.isValueChanging()) {

                // multiply duration by percentage calculated by slider position
                if (duration != null) {
                    this.player.seek(duration.multiply(timeSlider.getValue() / 100.0));
                }
                updateValues();
            }
        });
    }

    public void setPlayType(PlayType playType) {

        this.playType = playType;
        setInitiateControls(musicLayout);

    }

    public PlayType getPlayTypye() {
        return this.playType;
    }

    /**
     * Update values.
     */
    @SuppressWarnings("deprecation")
    protected void updateValues() {

        if (playTime != null && timeSlider != null && volumeSlider != null && duration != null) {

            Platform.runLater(() -> {

                Duration currentTime = this.player.getCurrentTime();

                playTime.setText(formatTime(currentTime, duration));

                timeSlider.setDisable(duration.isUnknown());

                if (!timeSlider.isDisabled() && duration.greaterThan(Duration.ZERO) && !timeSlider.isValueChanging()) {

                    timeSlider.setValue(currentTime.divide(duration).toMillis() * 100.0);
                }

                if (!volumeSlider.isValueChanging()) {

                    volumeSlider.setValue((int) Math.round(this.player.getVolume() * 100));
                }
            });
        }
    }

    /**
     * Format time.
     *
     * @param elapsed  the elapsed
     * @param duration the duration
     * @return the string
     */
    private String formatTime(Duration elapsed, Duration duration) {

        int intElapsed = (int) Math.floor(elapsed.toSeconds());

        int elapsedHours = intElapsed / (60 * 60);

        if (elapsedHours > 0) {

            intElapsed -= elapsedHours * 60 * 60;

        }

        int elapsedMinutes = intElapsed / 60;

        int elapsedSeconds = intElapsed - elapsedHours * 60 * 60 - elapsedMinutes * 60;

        if (duration.greaterThan(Duration.ZERO)) {

            int intDuration = (int) Math.floor(duration.toSeconds());

            int durationHours = intDuration / (60 * 60);

            if (durationHours > 0) {

                intDuration -= durationHours * 60 * 60;

            }

            int durationMinutes = intDuration / 60;

            int durationSeconds = intDuration - durationHours * 60 * 60 - durationMinutes * 60;

            if (durationHours > 0) {

                return String.format("%d:%02d:%02d/%d:%02d:%02d",

                        elapsedHours, elapsedMinutes, elapsedSeconds,

                        durationHours, durationMinutes, durationSeconds
                );

            } else {

                return String.format("%02d:%02d/%02d:%02d", elapsedMinutes, elapsedSeconds, durationMinutes, durationSeconds);

            }

        } else {

            if (elapsedHours > 0) {
                return String.format("%d:%02d:%02d", elapsedHours, elapsedMinutes, elapsedSeconds);
            } else {
                return String.format("%02d:%02d", elapsedMinutes, elapsedSeconds);

            }

        }

    }

    /**
     * Load file.
     *
     * @param songPath the song path
     */
    public void loadFile(String songPath) {

        this.songFile = new Media(songPath);
        this.player = new MediaPlayer(this.songFile);
        this.player.setVolume(currentlyUsedVolume);

        currentlyPlaying = songPath;

        setInitiateControls(musicLayout);
        updateValues();
    }

    /**
     * Load shuffled song.
     *
     * @return the integer
     */
    public Integer loadShuffledSong() {

        int diskSize = musicLayout.musicList.size();

        Random randomDisk = new Random();
        int random = randomDisk.nextInt(diskSize);

        if (playedSongs.isEmpty()) {

            playedSongs.push(random);

        } else {

            if (playedSongs.size() == diskSize) {

                Toast makeToast = new Toast(this.musicLayout.musicSkin);
                makeToast.setToastMessage("One last time!");
                this.setFinishedShuffles(true);
            } else {

                if (!playedSongs.contains(random)) {

                    playedSongs.push(random);

                } else {

                    int[] selections = new int[diskSize];
                    for (int i = 0; i < diskSize; i++) {
                        selections[i] = i;
                    }

                    for (int i = 0; i < diskSize; i++) {

                        if (!playedSongs.contains(i)) {

                            playedSongs.push(selections[i]);
                            break;
                        }

                    }

                }

            }

        }
        return this.playedSongs.peek();

    }

    public void loadShuffled() {

        int diskSize = musicLayout.musicList.size();

        Random randomDisk = new Random();
        int random = randomDisk.nextInt(diskSize);

        if (playedSongs.isEmpty()) {

            playedSongs.push(random);
            System.out.println(musicLayout.musicList.get(random).getArtist());

        } else if (playedSongs.size() == diskSize) {

            // this.stop();
            System.out.println("End of file");

        } else if (!playedSongs.contains(random)) {

            playedSongs.push(random);
            System.out.println(musicLayout.musicList.get(random).getArtist());
        } else {
            int[] selections = new int[diskSize];
            for (int i = 0; i < diskSize; i++) {
                selections[i] = i;
            }

            for (int i = 0; i < diskSize; i++) {
                if (!playedSongs.contains(i)) {
                    System.out.println(musicLayout.musicList.get(selections[i]).getArtist());
                    playedSongs.push(selections[i]);
                    break;
                }
            }
        }
        System.out.println("testing this function: " + playedSongs.peek());
    }

    /**
     * Checks if is finished shuffles.
     *
     * @return true, if is finished shuffles
     */
    public boolean isFinishedShuffles() {
        return isFinishedShuffles;
    }

    /**
     * Sets the finished shuffles.
     *
     * @param isFinishedShuffles the new finished shuffles
     */
    public void setFinishedShuffles(boolean isFinishedShuffles) {
        this.isFinishedShuffles = isFinishedShuffles;
    }

    /**
     * Gets the status.
     *
     * @return the status
     */
    public MediaPlayer.Status getStatus() {

        return this.player.getStatus();
    }

    /**
     * Gets the media.
     *
     * @return the media
     */
    public Media getMedia() {
        return this.player.getMedia();
    }

    /**
     * Play.
     */
    public void play() {
        this.player.play();
    }

    /**
     * Stop.
     */
    public void stop() {
        this.player.stop();
    }

    /**
     * Pause.
     */
    public void pause() {
        this.player.pause();
    }

    /**
     * Checks if it is playing.
     *
     * @param current_tray the current_tray
     * @return the boolean
     */
    public Boolean isPlaying(String current_tray) {

        return this.currentlyPlaying.equals(current_tray);
    }

    /**
     * Sets the status.
     *
     * @param status the new status
     */
    public void setStatus(MediaPlayer.Status status) {
        this.status = status;
    }

}
