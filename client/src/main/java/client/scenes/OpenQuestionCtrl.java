package client.scenes;

import client.scenes.controllerrequirements.QuestionRequirements;
import commons.Question;
import java.util.Scanner;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javax.inject.Inject;

public class OpenQuestionCtrl implements QuestionRequirements {

    private MainCtrl mainCtrl;
    private QuestionFrameCtrl questionFrameCtrl;
    private Question question;
    private int answer;

    @FXML
    Button submitButton;

    @FXML
    Text questionField;

    @FXML
    Text answerText;

    @FXML
    Text pointsText;

    @FXML
    TextField entryField;

    @FXML
    Text errorMessage;

    /**
     * Injects necessary dependencies
     *
     * @param mainCtrl          - the main front-ent controller
     * @param questionFrameCtrl - the scene into which it has to be injected
     */
    @Inject
    public OpenQuestionCtrl(MainCtrl mainCtrl, QuestionFrameCtrl questionFrameCtrl) {
        this.mainCtrl = mainCtrl;
        this.questionFrameCtrl = questionFrameCtrl;
    }


    /**
     * Reads the submitted answer and checks whether a number has actually been entered
     * Then changes the text on the button to 'Submitted!'
     */
    @FXML
    public void submit() {
        String ans = entryField.getText();
        Scanner scanner = new Scanner(ans);
        this.answer = scanner.nextInt();
        if (answer == 0) {
            errorMessage.setVisible(true);
        } else {
            errorMessage.setVisible(false);
            submitButton.setText("Submitted!");
            submitButton.setOnAction(null);
        }
    }

    /**
     * Initializes the question and sets the corresponding text as the question on screen
     *
     * @param question - the activity that this question is about
     */
    @Override
    public void initialize(Question question) {
        this.question = question;
        questionField.setText("How many Wh does " + question.getActivities().get(0).getTitle() + " take?");
    }

    /**
     * Reveals the correct answer by displaying what the correct answer was and printing the points.
     * The points are calculates as percentages off from the correct answer, so if you are 1% off you get 99/100 points,
     * 2% off gives you 98/100 points and 100% off leaves you with 0 points.
     */
    @Override
    public void revealCorrectAnswer() {
        int correctAnswer = this.question.getActivities().get(0).getConsumptionInWh();
        answerText.setText("It takes " + correctAnswer + "Wh!");
        int percentageOff = ((Math.abs(correctAnswer - this.answer)) / correctAnswer) * 100;
        int baseScore = 100 - percentageOff / 2;
        mainCtrl.addPoints(baseScore);
        pointsText.setText(""); //Should the addPoints method in MainCtrl return something that can be printed here?
    }

    ;
}
