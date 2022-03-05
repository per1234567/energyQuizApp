package client.scenes;

import client.scenes.controllerrequirements.QuestionRequirements;
import commons.Question;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javax.inject.Inject;

public class QuestionThreePicturesCtrl implements QuestionRequirements {

    private MainCtrl mainCtrl;
    private QuestionFrameCtrl questionFrameCtrl;
    private Question question;
    private List<Button> answers;
    private List<ImageView> images;
    private List<ImageView> wrong;
    private List<ImageView> correct;
    private int positionCorrectAnswer;
    private int selectedAnswerButton;

    @FXML
    Button answerA;

    @FXML
    Button answerB;

    @FXML
    Button answerC;

    @FXML
    TextField questionOutput;

    @FXML
    ImageView imageA;

    @FXML
    ImageView imageB;

    @FXML
    ImageView imageC;

    @FXML
    ImageView correctA;

    @FXML
    ImageView wrongA;

    @FXML
    ImageView correctB;

    @FXML
    ImageView wrongB;

    @FXML
    ImageView correctC;

    @FXML
    ImageView wrongC;

    @Inject
    public QuestionThreePicturesCtrl(MainCtrl mainCtrl, QuestionFrameCtrl questionFrameCtrl) {
        this.mainCtrl = mainCtrl;
        this.questionFrameCtrl = questionFrameCtrl;
    }

    @Override
    public void initialize(Question question) {
        this.question = question;
        this.answers = List.of(answerA, answerB, answerC);
        this.images = List.of(imageA, imageB, imageC);
        this.correct = List.of(correctA, correctB, correctC);
        this.wrong = List.of(wrongA, wrongB, wrongC);

        this.positionCorrectAnswer = question.getCorrectAnswer();
        this.questionOutput.setText(question.getQuestion());
        setAnswers(question);
        IntStream.range(0, correct.size()).forEach(i -> {
            correct.get(i).setVisible(false);
            wrong.get(i).setVisible(false);
            answers.get(i).setOpacity(1);
            answers.get(i).setStyle("-fx-border-color:  #5CB4BF");
        });
    }

    public void setAnswers(Question question) {
        for (int i = 0; i < question.getActivities().size(); i++) {
            String title = question.getActivities().get(i).title;
            String imagePath = question.getActivities().get(i).imagePath;
            Image image = new Image(imagePath, 200, 186, true, false);
            images.get(i).setImage(image);
            answers.get(i).setText(title);
        }
    }

    @FXML
    void answerASelected() {
        this.selectedAnswerButton = 0;
        setChosenAnswer();
    }

    @FXML
    void answerBSelected() {
        this.selectedAnswerButton = 1;
        setChosenAnswer();
    }

    @FXML
    void answerCSelected() {
        this.selectedAnswerButton = 2;
        setChosenAnswer();
    }

    private void setChosenAnswer() {
        answers.get(selectedAnswerButton).setStyle("-fx-border-color: #028090");
        for (int i = 0; i < 3; i++) {
            answers.get(i).setOnAction(null);
            if (i != selectedAnswerButton) {
                answers.get(i).setOpacity(0.5);
            }
        }
    }

    /**
     * reveals the correct answer by switching the visibility of the ticks and crosses
     */
    @Override
    public void revealCorrectAnswer() {
        correct.get(positionCorrectAnswer).setVisible(true);
        for (int i = 0; i < 3; i++) {
            if (i != positionCorrectAnswer) {
                wrong.get(i).setVisible(true);
            }
        }

        if (selectedAnswerButton == positionCorrectAnswer) {
            mainCtrl.addPoints(100);
        } else {
            mainCtrl.addPoints(0);
        }
    }

    public Question getQuestion() {
        return question;
    }

    public List<Button> getAnswers() {
        return answers;
    }

    public List<ImageView> getImages() {
        return images;
    }

    public int getPositionCorrectAnswer() {
        return positionCorrectAnswer;
    }

    public int getSelectedAnswerButton() {
        return selectedAnswerButton;
    }

    public Button getAnswerA() {
        return answerA;
    }

    public Button getAnswerB() {
        return answerB;
    }

    public Button getAnswerC() {
        return answerC;
    }

    public TextField getQuestionOutput() {
        return questionOutput;
    }

    public ImageView getImageA() {
        return imageA;
    }

    public ImageView getImageB() {
        return imageB;
    }

    public ImageView getImageC() {
        return imageC;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof QuestionThreePicturesCtrl)) {
            return false;
        }
        QuestionThreePicturesCtrl that = (QuestionThreePicturesCtrl) o;
        return positionCorrectAnswer == that.positionCorrectAnswer
            && selectedAnswerButton == that.selectedAnswerButton
            && Objects.equals(mainCtrl, that.mainCtrl)
            && Objects.equals(questionFrameCtrl, that.questionFrameCtrl)
            && Objects.equals(question, that.question) && Objects.equals(answers, that.answers)
            && Objects.equals(images, that.images) && Objects.equals(wrong, that.wrong)
            && Objects.equals(correct, that.correct) && Objects.equals(answerA, that.answerA)
            && Objects.equals(answerB, that.answerB) && Objects.equals(answerC, that.answerC)
            && Objects.equals(questionOutput, that.questionOutput)
            && Objects.equals(imageA, that.imageA) && Objects.equals(imageB, that.imageB)
            && Objects.equals(imageC, that.imageC) && Objects.equals(correctA, that.correctA)
            && Objects.equals(wrongA, that.wrongA) && Objects.equals(correctB, that.correctB)
            && Objects.equals(wrongB, that.wrongB) && Objects.equals(correctC, that.correctC)
            && Objects.equals(wrongC, that.wrongC);
    }
}
