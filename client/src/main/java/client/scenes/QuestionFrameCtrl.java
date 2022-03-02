package client.scenes;

import client.scenes.controllerrequirements.QuestionFrameFunctions;
import client.scenes.framecomponents.EmoteCtrl;
import client.scenes.framecomponents.TimerBarCtrl;
import client.utils.TimeUtils;
import commons.LeaderboardEntry;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javax.inject.Inject;

/**
 * Controller for questionFrame scene
 */
public class QuestionFrameCtrl implements Initializable, QuestionFrameFunctions {

    public boolean test = false;

    public static final int LEADERBOARD_SIZE_MAX = 5;

    final MainCtrl mainCtrl;
    private final TimerBarCtrl timerBarCtrl;
    private final EmoteCtrl emoteCtrl;
    TimeUtils timeUtils;

    @FXML
    public Rectangle timerBar;
    @FXML
    VBox sideLeaderboard;
    @FXML
    Pane centerContent;
    @FXML
    private Button trophy;
    @FXML
    private Button emote;
    @FXML
    private VBox reactionContainer;
    @FXML
    HBox emoticonSelectionField;
    @FXML
    private Button scoreField;
    @FXML
    private Text newPoints;
    @FXML
    private Button halveTime;
    @FXML
    private Button eliminateWrongAnswer;
    @FXML
    private Button doublePoints;
    @FXML
    private Button turnIndicator;
    @FXML
    public VBox helpMenuContainer;
    @FXML
    private Text helpPointsGained;
    @FXML
    private Button helpMenuScore;
    @FXML
    private Button helpMenuQuestionNumber;

    @FXML
    private TableView<LeaderboardEntry> leaderboard;
    @FXML
    private TableColumn<LeaderboardEntry, String> playerColumn;
    @FXML
    private TableColumn<LeaderboardEntry, String> scoreColumn;

    private List<Button> jokers;

    boolean isMultiplayerGame;
    private int gameScore;
    private int questionNumber;
    long lastEscapeKeyPressTime;


    /**
     * Injects necessary dependencies
     *
     * @param mainCtrl The main front-end controller
     */
    @Inject
    public QuestionFrameCtrl(MainCtrl mainCtrl, TimerBarCtrl timerBarCtrl, EmoteCtrl emoteCtrl, TimeUtils timeUtils) {
        this.mainCtrl = mainCtrl;
        this.timerBarCtrl = timerBarCtrl;
        this.emoteCtrl = emoteCtrl;
        this.timeUtils = timeUtils;
    }

    /**
     * Initializes this controller
     *
     * @param location  Location of this controller
     * @param resources (Potentially) useful resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        jokers = List.of(halveTime, eliminateWrongAnswer, doublePoints);

        timerBarCtrl.initialize(timerBar, timeUtils);
        emoteCtrl.initialize(reactionContainer, timeUtils);

        playerColumn.setCellValueFactory(e -> new SimpleStringProperty(e.getValue().getName()));
        scoreColumn.setCellValueFactory(e -> new SimpleStringProperty(e.getValue().getScoreString()));

        lastEscapeKeyPressTime = 0;

        // LINES BELOW ARE FOR DEMONSTRATION PURPOSES

        setRemainingTime(10);

        initializeMultiplayerGame(List.of("Per", "Andrei"));
        // startSingleplayerGame();
        addPoints(100);
    }

    /**
     * Resets the question frame and initializes settings for a new single-player game
     */
    @SuppressWarnings("unused")
    public void initializeSingleplayerGame() {
        startNewGame(false, null);
    }

    /**
     * Resets the question frame and initializes settings for a new multiplayer game
     *
     * @param names The names of all players involved
     */
    public void initializeMultiplayerGame(List<String> names) {
        startNewGame(true, names);
    }

    /**
     * Initializes settings for a new game
     *
     * @param isMultiplayerGame Whether this is a multiplayer game
     * @param playerNames       The names of the players involved
     */
    private void startNewGame(boolean isMultiplayerGame, List<String> playerNames) {
        this.isMultiplayerGame = isMultiplayerGame;
        this.gameScore = 0;
        this.questionNumber = 0;
        this.lastEscapeKeyPressTime = 0;

        incrementQuestionNumber();

        trophy.setManaged(isMultiplayerGame);
        trophy.setVisible(isMultiplayerGame);
        emote.setVisible(isMultiplayerGame);

        for (Button joker : jokers) {
            if (joker.getStyleClass().contains("usedJoker")) {
                joker.getStyleClass().add("clickable");
                joker.getStyleClass().remove("usedJoker");
            }
        }

        if (isMultiplayerGame) {
            List<LeaderboardEntry> players = playerNames
                .stream()
                .map(p -> new LeaderboardEntry(p, 0))
                .collect(Collectors.toList());
            setLeaderboardContents(players);
        } else {
            sideLeaderboard.setVisible(false);
            setEmoticonField(false);
            halveTime.getStyleClass().add("usedJoker");
            halveTime.getStyleClass().remove("clickable");
        }
    }

    /**
     * Sets node containing question at the center of the frame
     *
     * @param questionNode The node to be inserted in the center of the frame
     */
    public void setCenterContent(Node questionNode) {
        centerContent.getChildren().clear();
        centerContent.getChildren().add(questionNode);
    }

    /**
     * Add points to a player's score, as seen in top left
     *
     * @param points The number of points to be added
     */
    public void addPoints(int points) {
        newPoints.setText("+" + points);
        newPoints.setVisible(true);

        gameScore += points;
        scoreField.setText(((Integer) gameScore).toString());

        helpPointsGained.setText("+" + points);
        helpMenuScore.setText(((Integer) gameScore).toString());

        timeUtils.runAfterDelay(() -> newPoints.setVisible(false), 3);
    }

    /**
     * Increments the question number displayed in the top right
     */
    public void incrementQuestionNumber() {
        questionNumber++;
        turnIndicator.setText(questionNumber + "/20");
        helpMenuQuestionNumber.setText(questionNumber + "/20");
    }

    /**
     * Sets contents of the pop-up leaderboard
     *
     * @param entries A list of LeaderboardEntry objects representing leaderboard fields
     */
    public List<LeaderboardEntry> setLeaderboardContents(List<LeaderboardEntry> entries) {
        entries = entries.stream().sorted().collect(Collectors.toList());
        while (entries.size() > LEADERBOARD_SIZE_MAX) {
            entries.remove(entries.size() - 1);
        }

        if (!test) {
            ObservableList<LeaderboardEntry> data = FXCollections.observableList(entries);
            leaderboard.setItems(data);
        }

        return entries;
    }

    /**
     * Toggles visibility of the side leaderboard
     */
    @FXML
    void toggleLeaderboardVisibility() {
        if (isMultiplayerGame) {
            sideLeaderboard.setVisible(!sideLeaderboard.isVisible());
        }
    }

    /**
     * Toggles help menu visibility
     */
    @FXML
    public void toggleHelpMenuVisibility() {
        helpMenuContainer.setVisible(!helpMenuContainer.isVisible());
    }

    /**
     * Toggles emoticon field visibility
     */
    @FXML
    void toggleEmoticonField() {
        setEmoticonField(!emoticonSelectionField.isVisible());
    }

    /**
     * Turns off emoticon field
     */
    @FXML
    void toggleEmoticonFieldExit() {
        setEmoticonField(false);
    }

    /**
     * Turns emoticon field on or off
     *
     * @param visible Whether the emoticon field must become visible
     */
    void setEmoticonField(boolean visible) {
        if (!isMultiplayerGame) {
            return;
        }

        if (!visible) {
            emoticonSelectionField.setVisible(false);
            if (!test) {
                emote.getStyleClass().remove("jagged");
            }
        } else {
            emoticonSelectionField.setVisible(true);
            if (!test) {
                emote.getStyleClass().add("jagged");
            }
        }
    }

    /**
     * Methods to be run when a user chooses to send an emoticon
     * <p>
     * TODO: make these send a request to the server, delete placeholders
     */
    @FXML
    private void addHappyReaction() {
        displayNewEmoji("Chris", "happy");
    }

    @FXML
    private void addSadReaction() {
        displayNewEmoji("Per", "sad");
    }

    @FXML
    private void addAngryReaction() {
        displayNewEmoji("Mirella", "angry");
    }

    @FXML
    private void addSurprisedReaction() {
        displayNewEmoji("Andrei", "surprised");
    }

    /**
     * Adds a new reaction sent by another player to the right
     *
     * @param name     The name of the person sending the emote
     * @param reaction A string representing the emoticon (Accepted values: "happy", "sad", "angry", "surprised")
     */
    public void displayNewEmoji(String name, String reaction) {
        emoteCtrl.addReaction(name, reaction);
    }

    /**
     * Applies settings that disable the use of a joker
     *
     * @param joker The joker to be disabled
     * @return Whether the joker was already disabled
     */
    private boolean jokerUsed(Button joker) {
        if (joker.getStyleClass().contains("usedJoker")) {
            return true;
        }

        joker.getStyleClass().add("usedJoker");
        joker.getStyleClass().remove("clickable");
        return false;
    }

    /**
     * Makes timerBar slide from full to empty in a provided number of seconds
     *
     * @param seconds How long the bar should slide
     */
    public void setRemainingTime(double seconds) {
        timerBarCtrl.setRemainingTime(seconds);
    }

    /**
     * Halves the remaining time as seen on the timer bar
     */
    public void halveRemainingTime() {
        timerBarCtrl.halveRemainingTime();
    }

    /**
     * Sends a request to the server that a player used the halveTime joker
     * TODO: send a request to the server
     */
    @FXML
    private void halveTime() {
        if (jokerUsed(halveTime)) {
            return;
        }
    }

    /**
     * Sends a request to the server that a player used the doublePoints joker
     * TODO: send a request to the server
     */
    @FXML
    private void doublePoints() {
        if (jokerUsed(doublePoints)) {
            return;
        }
    }

    /**
     * Sends a request to the server that a player used the eliminateWrongAnswer joker
     * TODO: send a request to the server
     */
    @FXML
    private void eliminateWrongAnswer() {
        if (jokerUsed(eliminateWrongAnswer)) {
            return;
        }
    }

    /**
     * Provides functionality for keybindings to accelerate certain actions
     *
     * @param e Information about a keypress performed by the user
     *          <p>
     *          This should only be called by the MainCtrl showQuestionFrame method
     */
    public void keyPressed(KeyCode e) {
        switch (e) {
            case H:
                toggleHelpMenuVisibility();
                break;
            case T:
                halveTime();
                break;
            case E:
                eliminateWrongAnswer();
                break;
            case D:
                doublePoints();
                break;
            case L:
                toggleLeaderboardVisibility();
                break;
            case ESCAPE:
                long now = timeUtils.now();
                if (now - lastEscapeKeyPressTime < 200) {
                    mainCtrl.disconnect();
                }
                lastEscapeKeyPressTime = now;
                break;
            default:
                break;
        }
    }
}