package com.danko;

import com.danko.util.XLSXWriter;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.stage.Stage;
import com.danko.controller.MainController;
import com.danko.controller.PreliminaryController;
import com.danko.controller.QuestionController;
import com.danko.controller.ResultController;
import com.danko.controller.SpecialQuestionViewController;
import com.danko.model.Answer;
import com.danko.model.Question;
import com.danko.util.Util;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Main extends Application {

    private static final int ID_COLUMN_INDEX = 0;
    private static final int QUESTION_TEXT_COLUMN_INDEX = 1;
    private static final int ANSWER_OPTIONS_COLUMN_INDEX = 2;
    private static final int CANDIDATE_ANSWER_COLUMN_INDEX = 3;
    private static final int RIGHT_ANSWER_COLUMN_INDEX = 4;
    private static final int RESULT_COLUMN_INDEX = 5;
    private static final int POINTS_COLUMN_INDEX = 6;
    private static final short MAIN_ROW_HEIGHT = (short) (40 * 20);
    private static final short REGULAR_ROW_HEIGHT = (short) (50 * 20);
    private static final String FILE_NAME_PATTERN = "test_result_%s_%s.xlsx";


    private List<Question> questions;
    private Stage primaryStage;
    private String firstName;
    private String lastName;
    private QuestionController questionController;
    private SpecialQuestionViewController specialQuestionViewController;
    private XSSFWorkbook workbook;
    private int rows;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() throws Exception {
        super.init();
        questions = Util.initQuestions();
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        this.primaryStage = primaryStage;

        FXMLLoader fxmlLoader = new FXMLLoader();
        setMainScene(primaryStage, fxmlLoader);

        MainController mainController = fxmlLoader.getController();
        mainController.setMain(this);
    }

    private void setMainScene(Stage primaryStage, FXMLLoader fxmlLoader) throws IOException {
        fxmlLoader.setLocation(getClass().getResource("/view/MainView.fxml"));
        AnchorPane mainRootLayout = fxmlLoader.load();
        BackgroundImage image = new BackgroundImage(
                new Image(this.getClass().getResource("/img/uzor-4.jpg").toString(), 400, 0, true, true),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                new BackgroundPosition(
                        Side.RIGHT, 0, true,
                        Side.BOTTOM, 0, true),
                BackgroundSize.DEFAULT);
        mainRootLayout.setBackground(new Background(image));
        Scene scene = new Scene(mainRootLayout);
        primaryStage.setTitle("Тест");
        primaryStage.setResizable(false);
        primaryStage.getIcons().add(new Image(this.getClass().getResource("/img/logo.png").toString()));
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public void startTest() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            setNewSceneFromView(fxmlLoader, "/view/QuestionView.fxml");

            questionController = fxmlLoader.getController();
            questionController.setMain(this);

            questionController.startTimer();
            questionController.startSurvey(questions);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void setNewSceneFromView(FXMLLoader fxmlLoader, String viewName) throws IOException {
        fxmlLoader.setLocation(getClass().getResource(viewName));
        AnchorPane questionRootLayout = fxmlLoader.load();
        Scene scene = new Scene(questionRootLayout);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void endTest() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            setNewSceneFromView(fxmlLoader, "/view/ResultView.fxml");

            ResultController resultController = fxmlLoader.getController();
            resultController.setMain(this);

            double result = getResultPoints();
            resultController.showResult(result);

            new XLSXWriter().writeResults(questions, firstName, lastName, result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private double getResultPoints() {
        double result = 0;
        for (Question question : questions) {
            List<Answer> answers = question.getAnswers();
            List<Long> rightAnswersIds = question.getRightAnswersIds();
            double allSelectedAnswers = answers.stream()
                    .filter(Answer::isSelected)
                    .count();
            double allRightNotSelectedAnswers = answers.stream()
                    .filter(answer -> !answer.isSelected()
                            && rightAnswersIds.contains(answer.getId()))
                    .count();
            double allRightAnswers = answers.stream()
                    .filter(answer -> answer.isSelected()
                            && rightAnswersIds.contains(answer.getId()))
                    .count();
            double score = (1 / (allSelectedAnswers + allRightNotSelectedAnswers)) * allRightAnswers;
            if (!Double.isNaN(score)) {
                result += score;
            } else {
                score = 0;
            }
            question.setScore(score);
        }
        return result;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void startPreliminaryPage() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            setNewSceneFromView(fxmlLoader, "/view/PreliminaryView.fxml");

            PreliminaryController preliminaryController = fxmlLoader.getController();
            preliminaryController.setMain(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public AnchorPane getSpecialQuestionPane() {
        AnchorPane questionRootLayout = null;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/view/Question29View.fxml"));
            questionRootLayout = fxmlLoader.load();

            specialQuestionViewController = fxmlLoader.getController();
            specialQuestionViewController.setMain(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return questionRootLayout;
    }

    public void onSpecialQuestionDateSelected(LocalDate value) {
        if (questionController != null) {
            questionController.onSpecialQuestionDateSelected(value);
        }
    }

    public void setSpecialQuestionAnswerValue(String answerText) {
        if(questionController != null) {
            specialQuestionViewController.setSpecialQuestionAnswerValue(answerText);
        }
    }
}
