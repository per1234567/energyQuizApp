/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package client;

import static com.google.inject.Guice.createInjector;

import client.scenes.AddActivityDialogCtrl;
import client.scenes.AdminInterfaceCtrl;
import client.scenes.ExitPopUpCtrl;
import client.scenes.FinalScreenCtrl;
import client.scenes.LeaderboardCtrl;
import client.scenes.LobbyCtrl;
import client.scenes.MainCtrl;
import client.scenes.MainFrameCtrl;
import client.scenes.QuestionFrameCtrl;
import client.scenes.WaitingScreenCtrl;
import client.scenes.questioncontrollers.InsteadOfQuestionCtrl;
import client.scenes.questioncontrollers.OpenQuestionCtrl;
import client.scenes.questioncontrollers.QuestionOneImageCtrl;
import client.scenes.questioncontrollers.QuestionThreePicturesCtrl;
import client.scenes.questioncontrollers.QuestionTrueFalseCtrl;
import client.utils.GameUtils;
import client.utils.LobbyUtils;
import client.utils.ServerUtils;
import client.utils.TimeUtils;
import com.google.inject.Injector;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * The Main class
 */
public class Main extends Application {

    private static final Injector INJECTOR = createInjector(new MyModule());
    private static final MyFXML FXML = new MyFXML(INJECTOR);

    /**
     * Getter for MyFXML field
     *
     * @return MyFXML
     * <p>
     * This is needed because the question frame loads emojis sent by other players as new nodes
     */
    public static MyFXML getLoader() {
        return FXML;
    }

    /**
     * Hands control over to JavaFX
     *
     * @param args Arguments for starting the program
     */

    public static void main(String[] args) {
        launch();
    }

    /**
     * Loads all scenes/nodes, initializes main controller and configures primary (and only) stage
     *
     * @param primaryStage The stage containing all scenes
     */
    @Override
    public void start(Stage primaryStage) {

        var mainCtrl = INJECTOR.getInstance(MainCtrl.class);

        var serverUtils = INJECTOR.getInstance(ServerUtils.class);

        var timeUtils = INJECTOR.getInstance(TimeUtils.class);

        var gameUtils = INJECTOR.getInstance(GameUtils.class);

        var lobbyUtils = INJECTOR.getInstance(LobbyUtils.class);

        var mainFrame =
            FXML.load(MainFrameCtrl.class, "client/scenes/MainFrame.fxml");

        var lobbyFrame =
            FXML.load(LobbyCtrl.class, "client/scenes/LobbyFrame.fxml");

        var leaderboard =
            FXML.load(LeaderboardCtrl.class, "client/scenes/Leaderboard.fxml");

        var adminInterface =
            FXML.load(AdminInterfaceCtrl.class, "client/scenes/AdminInterface.fxml");

        var questionFrame =
            FXML.load(QuestionFrameCtrl.class, "client/scenes/questionFrame.fxml");

        var questionTrueFalse = FXML.load(QuestionTrueFalseCtrl.class, "client/scenes/QuestionTrueFalse.fxml"
        );

        var openQuestion =
            FXML.load(OpenQuestionCtrl.class, "client/scenes/OpenQuestion.fxml");

        var questionThreePictures =
            FXML.load(QuestionThreePicturesCtrl.class, "client/scenes/QuestionThreePictures.fxml"
            );

        var questionOneImage = FXML.load(QuestionOneImageCtrl.class, "client/scenes/QuestionOneImage.fxml"
        );

        var insteadOfQuestion =
            FXML.load(InsteadOfQuestionCtrl.class, "client/scenes/InsteadOfQuestion.fxml"
            );

        var finalScreen = FXML.load(FinalScreenCtrl.class,
            "client/scenes/FinalSingleplayerScreen.fxml");

        var waitingScreen = FXML.load(WaitingScreenCtrl.class, "client/scenes/WaitingScreen.fxml");

        var exitPopUp = FXML.load(ExitPopUpCtrl.class, "client/scenes/ExitPopUp.fxml");

        var addActivityDialog = FXML.load(AddActivityDialogCtrl.class, "client/scenes/AddActivityDialog.fxml");

        mainCtrl.initialize(serverUtils, gameUtils,
            lobbyUtils, timeUtils,
            primaryStage, mainFrame,
            lobbyFrame, leaderboard,
            adminInterface, questionFrame,
            questionTrueFalse, openQuestion,
            questionThreePictures, questionOneImage,
            insteadOfQuestion, finalScreen,
            addActivityDialog, waitingScreen, exitPopUp);
    }
}