package application;

import java.io.File;
import java.nio.file.Path;

import component.*;
import decoder.CommonCodec;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Reflection;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.MusicFile;
import model.PlayType;

/**
 * The Class MusicLayout.
 */
public class MusicLayout extends BorderPane {

    /**
     * The music skin.
     */
    public StackPane musicSkin;

    /**
     * The music list.
     */
    public ObservableList<MusicFile> musicList;

    /**
     * The music view.
     */
    public ListView<MusicFile> musicView;

    /**
     * The help, more.
     */
    private MiniControl more, helpMore;

    /**
     * The bottom container.
     */
    HBox bottomContainer;

    /**
     * The bottom view.
     */
    CommonCodec bottomView;

    /**
     * The play all.
     */
    public FloatingButton playAll;

    /**
     * The title mock.
     */
    Text title, titleMock;

    /**
     * The album art mock.
     */
    ImageView albumArtMock;

    /**
     * The selected music album art.
     */
    Image selectedMusicAlbumArt;

    /**
     * The music options.
     */
    ContextMenu musicOptions;

    /**
     * The mock dock.
     */
    BorderPane mockDock;

    /**
     * The previous and the next button controls.
     */
    MiniControl next, previous;

    /**
     * The fab.
     */
    Group FAB;

    /**
     * The selected file.
     */
    MusicFile selectedFile;

    /**
     * The pullUp more.
     */
    TranslateTransition pullUpMore;

    /**
     * The pullUp help more.
     */
    TranslateTransition pullUpHelpMore;

    private boolean isShuffle;

    /**
     * Instantiates a new music layout.
     */
    public MusicLayout(Stage stage) {
        closingRequest(stage);

        this.getStylesheets().add(MusicPlayer.STYLESHEET_MUSIC);
        this.musicList = FXCollections.observableArrayList();
        this.musicView = new ListView<>();
        ImageView nothingness = new ImageView(new Image("/images/ic_disc_full_black_36dp.png"));
        nothingness.setEffect(new Reflection());
        this.musicView.setPlaceholder(nothingness);

        this.musicView.setOnMouseClicked(e -> {
            if (e.isControlDown()) {

                try {
                    if (bottomView.getStatus() == MediaPlayer.Status.PLAYING) {
                        bottomView.stop();

                        this.playSelectedSong();

                    } else {
                        this.playSelectedSong();
                    }

                } catch (Exception e1) {
                    e1.printStackTrace();

                }

            }

        });

        MenuItem playThis = new MenuItem();
        playThis.setOnAction(e -> {

            try {
                if (bottomView.getStatus() == MediaPlayer.Status.PLAYING) {
                    bottomView.stop();
                    this.playSelectedSong();

                } else {
                    this.playSelectedSong();

                }
            } catch (Exception ignored) {

            }
        });
        ImageView playThisIcon = new ImageView(new Image("/images/ic_playlist_play_black_36dp.png"));
        playThisIcon.setFitHeight(30);
        playThisIcon.setFitWidth(30);
        playThis.setGraphic(playThisIcon);
        playThis.setText("Play Song");

        MenuItem removeThis = new MenuItem();
        removeThis.setOnAction(e -> {
            this.musicView.getItems().remove(this.musicView.getSelectionModel().getSelectedIndex());
            if (this.musicView.getItems().size() > 0) {
                this.musicView.setContextMenu(musicOptions);

            } else {
                this.musicView.setContextMenu(null);
            }

        });
        ImageView removeThisIcon = new ImageView(new Image("/images/ic_remove_from_queue_black_36dp.png"));
        removeThisIcon.setFitHeight(30);
        removeThisIcon.setFitWidth(30);
        removeThis.setGraphic(removeThisIcon);
        removeThis.setText("Remove Song");

        musicOptions = new ContextMenu();
        musicOptions.setConsumeAutoHidingEvents(true);
        musicOptions.getItems().addAll(playThis, removeThis);

        this.musicView.getStyleClass().add("music-view");
        this.musicSkin = new StackPane();
        this.setTop(this.appBar());
        this.setCenter(this.musicSkin);

        this.setMusicList();
        setShuffle(false);
        // this bottomContainer must always come after the musicList function to
        // avoid nullPointerException
        bottomContainer();
        this.setBottom(this.bottomContainer());

    }

    /**
     * Play selected song.
     */
    private void playSelectedSong() {

        selectedFile = this.musicView.getSelectionModel().getSelectedItem();
        bottomView.loadFile(this.selectedFile.getFilePath());
        selectedMusicAlbumArt = selectedFile.getAlbumArt();

        if (bottomView.isPlaying(selectedFile.getFilePath())) {

            for (MusicFile file : musicList) {
                file.playingLogo.setVisible(false);
                selectedFile.playingLogo.setVisible(true);
            }
        }
        bottomView.playButton.fire();
        bottomView.play();
        bottomView.volumeControls.setVisible(true);
        bottomView.miniControl.setVisible(true);
        if (this.title != null) {
            updateMusicDetails();
        }
    }

    /**
     * App bar.
     *
     * @return the top bar
     */
    public VBox appBar() {
        VBox topBarContainer = new VBox();
        topBarContainer.setSpacing(0);
        HBox appBar = new HBox();
        appBar.setEffect(new DropShadow());
        appBar.setAlignment(Pos.CENTER);
        appBar.getStyleClass().add("app_bar");
        appBar.setMinHeight(50);
        appBar.setMinWidth(355);

        helpMore = new MiniControl("/images/ic_help_outline_white_36dp.png");
        helpMore.setOnAction(e -> help());
        helpMore.setTranslateX(-210);

        ImageView icon = new ImageView(new Image("/images/ic_headset_white_36dp.png"));
        icon.setFitHeight(35);
        icon.setFitWidth(35);

        Text appName = new Text("Echo Player");
        appName.getStyleClass().add("app_name");
        // appName.setTranslateX(75);

        more = new MiniControl("/images/ic_more_vert_white_36dp.png");
        more.setOnAction(e -> options());
        more.setTranslateX(210);

        appBar.getChildren().addAll(icon, appName, more);

        ImageView image = new ImageView(new Image("/images/bar.png"));
        image.setFitHeight(165);
        image.setFitWidth(665);

        topBarContainer.getChildren().addAll(appBar, image);
        return topBarContainer;
    }

    /**
     * Options.
     */
    public void options() {

        ScaleTransition scale = new ScaleTransition(Duration.seconds(0.7), FAB);
        scale.setToX(0);
        scale.setToY(0);
        scale.play();

        pullUpControls();

        this.musicView.setDisable(true);
        this.bottomView.miniControl.setVisible(false);
        this.bottomView.playButton.setDisable(true);
        // this.FAB.setDisable(true);
        BorderPane options = new BorderPane();
        options.setTranslateX(90);
        options.setEffect(new DropShadow());
        options.getStyleClass().add(MusicPlayer.STYLESHEET_MUSIC);
        options.setMaxSize(200, 300);

        TranslateTransition translateTransition2 = new TranslateTransition(Duration.seconds(0.7), options);

        translateTransition2.setFromY(900);
        translateTransition2.setToY(-220);

        HBox closeStage = new HBox();
        closeStage.getStyleClass().add("close-bar");
        closeStage.setMinHeight(40);
        closeStage.setAlignment(Pos.CENTER_LEFT);
        closeStage.setMinWidth(400);
        options.setTop(closeStage);

        MiniControl close_menu = new MiniControl("/images/ic_close_white_36dp.png");
        close_menu.getStyleClass().add("close_button_material");
        close_menu.setTranslateX(290);
        close_menu.setOnMouseClicked(e -> {
            scale.setToX(1);
            scale.setToY(1);
            scale.play();

            pullDownControls();

            this.musicView.setDisable(false);
            this.bottomView.playButton.setDisable(false);
            this.bottomView.miniControl.setVisible(true);
            this.FAB.setDisable(false);
            translateTransition2.setFromY(-210);
            translateTransition2.setToY(500);
            translateTransition2.setOnFinished(e2 -> {

                try {
                    musicSkin.getChildren().remove(options);

                } catch (Exception ex) {
                    // e2.printStackTrace();
                }
            });
            translateTransition2.play();

        });

        Text title_name = new Text("Options");
        title_name.getStyleClass().add("option-title");
        title_name.setTranslateX(160);

        closeStage.getChildren().addAll(title_name, close_menu);

        // the center container
        BorderPane centerContainer = new BorderPane();
        centerContainer.getStyleClass().add("options-container");
        ListView<OptionsMenu> optionList = new ListView<>();
        optionList.getStyleClass().add("menu-option");
        // adding the menu items
        OptionsMenu addAFile = new OptionsMenu("Add a music File", "/images/ic_library_add_black_36dp.png");
        addAFile.setOnMouseClicked(e -> {
            FileChooser filechooser = new FileChooser();
            filechooser.getExtensionFilters().addAll(new ExtensionFilter("Music Files", "*.mp3"));
            File choosen = filechooser.showOpenDialog(null);

            if (!(choosen == null)) {
                try {
                    Path th = choosen.toPath();

                    String filePath = th.toString().replace('\\', '/');

                    this.addAllMusicFiles(new MusicFile(new File(filePath)));
                } catch (Exception ignored) {

                }
                Toast makeToast = new Toast(this.musicSkin);
                makeToast.setToastMessage("Song added!");
            }
        });
        OptionsMenu addAFolder = new OptionsMenu("Add a music Folder", "/images/ic_create_new_folder_black_36dp.png");
        addAFolder.setOnMouseClicked(e -> {

            DirectoryChooser dir = new DirectoryChooser();
            dir.setTitle("Select a music folder");
            File directory = dir.showDialog(null);
            Integer counter = 0;
            if (directory != null) {
                try {
                    File[] musicFiles = directory.listFiles();

                    assert musicFiles != null;
                    for (File music : musicFiles) {
                        if (music.getName().endsWith(".mp3")) {
                            String filePath = music.toString().replace('\\', '/');

                            this.addAllMusicFiles(new MusicFile(new File(filePath)));
                            counter++;
                        }
                    }
                    Toast makeToast = new Toast(this.musicSkin);
                    makeToast.setToastMessage(counter + " file(s) were added!");
                } catch (Exception eq) {
                    eq.printStackTrace();

                }

            }
        });

        OptionsMenu clearTray = new OptionsMenu("Clear music Tray", "/images/ic_delete_black_36dp.png");
        clearTray.setOnMouseClicked(e -> {

            Toast makeToast = new Toast(this.musicSkin);
            if (this.musicList.size() > 0) {
                musicList.clear();
                bottomView.stop();
                bottomView.player.dispose();

                makeToast.setToastMessage("music list Cleared");
                this.musicView.setContextMenu(null);
                bottomView.playedSongs.clear();

            } else {
                makeToast.setToastMessage("Music list is empty!");
            }

        });
        optionList.getItems().addAll(addAFile, addAFolder, clearTray);

        centerContainer.setCenter(optionList);
        options.setCenter(centerContainer);
        StackPane.setAlignment(options, Pos.CENTER);
        musicSkin.getChildren().add(options);
        translateTransition2.play();
    }

    /**
     * Adds the all music files.
     *
     * @param musicFile the music file
     */
    public void addAllMusicFiles(MusicFile musicFile) {
        this.musicView.getItems();
        this.musicView.setContextMenu(musicOptions);

        this.musicList.addAll(musicFile);
        this.musicView.setItems(this.musicList);

    }

    /**
     * Sets the music list.
     */
    public void setMusicList() {
        BorderPane musicListContainer = new BorderPane();
        this.musicView.setItems(this.musicList);

        musicListContainer.setCenter(this.musicView);

        FAB = new Group();
        FAB.setTranslateY(-40);
        FAB.setTranslateX(240);
        StackPane.setAlignment(FAB, Pos.TOP_CENTER);
        playAll = new FloatingButton("/images/ic_play_arrow_black_48dp.png");
        FAB.getChildren().add(playAll);

        this.musicSkin.getChildren().addAll(musicListContainer, FAB);
        StackPane.setAlignment(musicListContainer, Pos.CENTER);
    }

    /**
     * Bottom container.
     *
     * @return the h box
     */
    public HBox bottomContainer() {
        bottomView = new CommonCodec(this);
        bottomView.miniControl.setOnAction(e -> {

            if (this.musicSkin.getChildren().contains(mockDock)) {
                bottomView.miniControl.setVisible(false);

            } else {
                mock();
            }

        });
        playAll.setOnAction(e -> bottomView.playButton.fire());
        bottomContainer = new HBox();
        bottomContainer.setCursor(Cursor.HAND);

        bottomContainer.getChildren().add(bottomView);
        bottomContainer.setEffect(new DropShadow());
        bottomContainer.getStyleClass().add("bottom-container");
        bottomContainer.setMinHeight(60);
        bottomContainer.setMinWidth(355);

        return bottomContainer;
    }

    /**
     * Mock.
     */
    public void mock() {

        this.musicView.setDisable(true);
        bottomView.miniControl.setVisible(false);
        mockDock = new BorderPane();

        mockDock.setEffect(new DropShadow());

        // pull up more
        pullUpControls();

        mockDock.getStyleClass().add(MusicPlayer.STYLESHEET_MUSIC);
        mockDock.setMaxSize(400, 700);

        TranslateTransition translateTransition2 = new TranslateTransition(Duration.seconds(0.8), mockDock);
        translateTransition2.setFromY(500);
        translateTransition2.setToY(-70);

        TranslateTransition fabMove = new TranslateTransition(Duration.seconds(0.7), FAB);
        fabMove.setToY(-150);
        fabMove.setToX(0);
        fabMove.play();

        HBox closeStage = new HBox();
        closeStage.getStyleClass().add("close-bar");
        closeStage.setMinHeight(45);
        closeStage.setAlignment(Pos.CENTER_LEFT);
        closeStage.setMinWidth(musicSkin.getWidth() - 100);
        mockDock.setTop(closeStage);

        NavigationButton close_menu = new NavigationButton("/images/ic_fullscreen_exit_white_36dp.png");
        close_menu.setOnMouseClicked(e -> {

            pullDownControls();
            bottomView.setVisible(true);
            bottomView.miniControl.setVisible(true);
            this.musicView.setDisable(false);
            fabMove.setToY(-40);
            fabMove.setToX(240);
            fabMove.play();

            translateTransition2.setFromY(-70);
            translateTransition2.setToY(500);
            translateTransition2.play();
            translateTransition2.setOnFinished(event -> {

                try {
                    musicSkin.getChildren().remove(mockDock);

                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            });

        });

        close_menu.setAlignment(Pos.CENTER_LEFT);

        Text title_name = new Text("Now Playing!");
        title_name.getStyleClass().add("option-title");
        title_name.setTranslateX(180);

        HBox togglePlayType = new HBox();
        togglePlayType.setSpacing(10);
        ToggleGroup toggleType = new ToggleGroup();

        ToggleControl btnShuffle = new ToggleControl("/images/ic_shuffle_white_36dp.png");
        btnShuffle.setSelected(true);
        btnShuffle.setOnAction(e -> {
            if (btnShuffle.isSelected()) {
                setShuffle(true);
                this.bottomView.setPlayType(PlayType.SHUFFLE);
                this.bottomView.setFinishedShuffles(false);

            } else {
                setShuffle(false);
                this.bottomView.setPlayType(PlayType.NORMAL);
                this.bottomView.setFinishedShuffles(true);
                this.bottomView.playedSongs.clear();
            }
        });

        if (isShuffle()) {
            btnShuffle.setSelected(isShuffle);

        } else {

            btnShuffle.setSelected(isShuffle);
        }

        toggleType.getToggles().addAll(btnShuffle);
        togglePlayType.getChildren().addAll(btnShuffle);

        MiniControl shuffle = new MiniControl("/images/ic_shuffle_white_36dp.png");
        shuffle.setOnAction(e -> {
            bottomView.loadShuffledSong();
            shuffle.setGraphics("/images/ic_shuffle_white_36dp_selected.png");

        });

        togglePlayType.setTranslateX(title_name.getTranslateX() + 170);

        closeStage.getChildren().addAll(close_menu, title_name, togglePlayType);

        BorderPane controlsView = new BorderPane();
        controlsView.getStyleClass().add("mock-container");
        // controlsView.setPadding(new Insets(10,0,0,0));

        albumArtMock = new ImageView(bottomView.albumArt.getImage());
        albumArtMock.setTranslateY(-30);
        albumArtMock.setFitHeight(170);
        albumArtMock.setFitWidth(290);
        albumArtMock.setEffect(new Reflection());

        HBox controls = new HBox();
        controls.setVisible(false);
        if (this.musicList.size() > 1) {
            controls.setVisible(true);
        }
        controls.setSpacing(70);
        controls.setAlignment(Pos.CENTER);
        next = new MiniControl("/images/ic_skip_next_white_36dp.png");
        next.setOnAction(e -> next());
        next.setScaleX(1.5);
        next.setScaleY(1.5);
        previous = new MiniControl("/images/ic_skip_previous_white_36dp.png");
        previous.setOnAction(e -> previous());
        previous.setScaleX(1.5);
        previous.setScaleY(1.5);
        controls.getChildren().addAll(previous, next);
        controlsView.setBottom(controls);

        controlsView.setCenter(albumArtMock);

        HBox songDetails = new HBox();
        songDetails.setAlignment(Pos.CENTER);
        // songDetails.setEffect(new DropShadow());
        songDetails.getStyleClass().add("bottom-container");
        songDetails.setMinHeight(60);
        titleMock = new Text(bottomView.getTitle());
        titleMock.setTextAlignment(TextAlignment.CENTER);

        songDetails.getChildren().addAll(titleMock);

        mockDock.setCenter(controlsView);
        mockDock.setTop(closeStage);
        mockDock.setBottom(songDetails);

        StackPane.setAlignment(mockDock, Pos.CENTER);
        musicSkin.getChildren().add(mockDock);
        translateTransition2.play();

        if ((this.musicView.getSelectionModel().getSelectedIndex() + 1) == this.musicList.size()) {

            next.setVisible(false);

        } else if (this.musicView.getSelectionModel().getSelectedIndex() == 0) {
            previous.setVisible(false);

        } else {
            next.setVisible(true);
            previous.setVisible(true);
        }

    }

    /**
     * Pull up controls.
     */
    public void pullUpControls() {

        pullUpMore = new TranslateTransition(Duration.seconds(0.7), this.more);

        pullUpHelpMore = new TranslateTransition(Duration.seconds(0.7), this.helpMore);
        pullUpMore.setToY(-50);
        pullUpMore.play();

        pullUpHelpMore.setToY(-50);
        pullUpHelpMore.play();
    }

    /**
     * Pull down controls.
     */
    public void pullDownControls() {
        pullUpMore.setToY(0);
        pullUpMore.play();

        pullUpHelpMore.setToY(0);
        pullUpHelpMore.play();
    }

    /**
     * Next.
     */
    public void next() {
        try {
            previous.setVisible(true);
            bottomView.stop();
            int counter = this.musicView.getSelectionModel().getSelectedIndex() + 1;
            this.musicView.getSelectionModel().select(counter);
            MusicFile toPlay = this.musicList.get(counter);
            bottomView.loadFile(toPlay.getFilePath());

            if ((counter + 1) == this.musicList.size()) {

                next.setVisible(false);
                previous.setVisible(true);
            } else {
                next.setVisible(true);
            }

            if (bottomView.isPlaying(toPlay.getFilePath())) {

                for (MusicFile file : musicList) {
                    file.playingLogo.setVisible(false);
                    toPlay.playingLogo.setVisible(true);
                }
            }

            if (toPlay.getAlbumArt().isError()) {
                albumArtMock.setImage(new Image("/images/ic_headset_black_36dp.png"));
            } else {

                if (albumArtMock != null) {
                    albumArtMock.setImage(toPlay.getAlbumArt());
                    titleMock.setText(toPlay.getFileName());
                }
            }

            bottomView.playButton.fire();
            bottomView.play();
        } catch (IndexOutOfBoundsException e1) {
            Toast makeToast = new Toast(this.musicSkin);
            makeToast.setToastMessage("Last song!");
        }
    }

    /**
     * Previous.
     */
    public void previous() {
        try {
            next.setVisible(true);
            int counter = this.musicView.getSelectionModel().getSelectedIndex() - 1;
            this.musicView.getSelectionModel().select(counter);
            MusicFile toPlay = this.musicList.get(counter);
            bottomView.stop();
            bottomView.loadFile(toPlay.getFilePath());

            if (counter == 0) {

                previous.setVisible(false);
                next.setVisible(true);
            } else {
                previous.setVisible(true);
            }

            if (bottomView.isPlaying(toPlay.getFilePath())) {

                for (MusicFile file : musicList) {
                    file.playingLogo.setVisible(false);
                    toPlay.playingLogo.setVisible(true);
                }
            }

            if (toPlay.getAlbumArt().isError()) {
                albumArtMock.setImage(new Image("/images/ic_headset_black_36dp.png"));
            } else {

                if (albumArtMock != null) {
                    albumArtMock.setImage(toPlay.getAlbumArt());
                    titleMock.setText(toPlay.getFileName());
                }
            }

            titleMock.setText(toPlay.getFileName());
            bottomView.playButton.fire();
            bottomView.play();

        } catch (Exception e1) {

            Toast makeToast = new Toast(this.musicSkin);
            makeToast.setToastMessage("First song!");
        }
    }

    /**
     * Update music details.
     */
    public void updateMusicDetails() {

        if (albumArtMock != null) {
            albumArtMock.setImage(bottomView.albumArt.getImage());
            titleMock.setText(bottomView.getTitle());
        }
    }

    /**
     * Help.
     */
    public void help() {
        pullUpControls();
        BorderPane container = new BorderPane();
        container.getStyleClass().add("help-pane");
        container.setEffect(new DropShadow());
        StackPane.setAlignment(container, Pos.CENTER);
        container.setMaxWidth(300);
        container.setMaxHeight(250);

        TranslateTransition slideIn = new TranslateTransition(Duration.seconds(0.5), container);
        slideIn.setFromY(0);
        slideIn.setToY(-200);
        slideIn.play();

        HBox closeStage = new HBox();
        closeStage.getStyleClass().add("close-bar");
        closeStage.setMinHeight(45);
        closeStage.setAlignment(Pos.CENTER_LEFT);
        closeStage.setMinWidth(400);

        MiniControl close_menu = new MiniControl("/images/ic_close_white_36dp.png");
        close_menu.setOnMouseClicked(e -> {
            pullDownControls();
            slideIn.setFromY(-200);
            slideIn.setToY(200);

            slideIn.setOnFinished(close -> this.musicSkin.getChildren().remove(container));
            slideIn.play();
        });
        close_menu.setAlignment(Pos.CENTER_LEFT);

        Text title_name = new Text("About Us");
        title_name.getStyleClass().add("option-title");
        title_name.setTranslateX(90);

        closeStage.getChildren().addAll(close_menu, title_name);
        container.setTop(closeStage);

        // adding an image view plus profile picture
        StackPane imageContainer = new StackPane();
        imageContainer.getStyleClass().add("profile-image-container");
        ImageView profile = new ImageView(new Image("/images/front-screen.png"));
        profile.setFitHeight(50);
        profile.setFitWidth(50);
        imageContainer.setShape(new Circle(100));
        profile.setClip(new Circle(100));
        imageContainer.getChildren().add(profile);
        container.setCenter(imageContainer);

        this.musicSkin.getChildren().add(container);
    }

    /**
     * Checks if is shuffle.
     *
     * @return true, if is shuffled
     */
    public boolean isShuffle() {
        return isShuffle;
    }

    /**
     * Sets the shuffle.
     *
     * @param isShuffle the new shuffle
     */
    public void setShuffle(boolean isShuffle) {
        this.isShuffle = isShuffle;
    }

    public void nextShuffle(Integer nextValue) {

        // work on the controls visibilities as they can cause very crucial
        // error and possible
        // shutdown of the whole EchoPlayer
        try {
            previous.setVisible(true);
            bottomView.stop();

            this.musicView.getSelectionModel().select(nextValue);
            MusicFile toPlay = this.musicList.get(nextValue);
            bottomView.loadFile(toPlay.getFilePath());

            if ((nextValue + 1) == this.musicList.size()) {

                next.setVisible(false);
                previous.setVisible(true);
            } else if (nextValue == 0) {
                previous.setVisible(false);
                next.setVisible(true);
            } else {
                next.setVisible(true);
            }

            if (bottomView.isPlaying(toPlay.getFilePath())) {

                for (MusicFile file : musicList) {
                    file.playingLogo.setVisible(false);
                    toPlay.playingLogo.setVisible(true);
                }
            }

            if (toPlay.getAlbumArt().isError()) {
                albumArtMock.setImage(new Image("/images/ic_headset_black_36dp.png"));
            } else {

                if (albumArtMock != null) {
                    albumArtMock.setImage(toPlay.getAlbumArt());
                    titleMock.setText(toPlay.getFileName());
                }
            }

            bottomView.playButton.fire();
            bottomView.play();
        } catch (IndexOutOfBoundsException e1) {
            Toast makeToast = new Toast(this.musicSkin);
            makeToast.setToastMessage("Last song!");
        }
    }

    public void closingRequest(Stage stageManager) {

        stageManager.setOnCloseRequest(e -> {
            e.consume();
            Alert closeAlert = new Alert(musicSkin);

            // initializing the action buttons
            closeAlert.acceptClose.setOnAction(close -> {
                closeAlert.progressContainer.setVisible(true);

                closeAlert.task.playFromStart();

                closeAlert.task.setOnFinished(eClose -> {
                    try {
                        Thread.sleep(200);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    // primaryStage.close();
                    // exit the application as needed
                    Platform.exit();
                });

            });

            closeAlert.cancelClose.setOnAction(cancel -> closeAlert.fadeOut.playFromStart());
        });
    }

}