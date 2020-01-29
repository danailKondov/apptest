package com.danko.controller;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import com.danko.Main;
import com.danko.model.Answer;
import com.danko.model.Question;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class QuestionController {

    private static final int TEST_TIME_LIMIT_IN_MINUTES = 30;

    @FXML
    private Label timer;

    @FXML
    private Label topMessageText;

    @FXML
    private VBox bottomVBox;

    @FXML
    private AnchorPane bottomPane;

    private Main main;
    private int seconds;
    private int minutes;
    private int numOfQuestion;
    private Map<Long, List<Answer>> result = new HashMap<>();
    private List<Question> questions;
    private AnchorPane specialQuestionPane;
    private final int ID_OF_SPECIAL_QUESTION = 29;

    public void setMain(Main main) {
        this.main = main;
    }

    public void startTimer() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), evt -> updateTime()));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void updateTime() {
        seconds++;
        if (seconds == 60) {
            seconds = 0;
            minutes++;
            showWarningMessage();
        }
        timer.setText(String.format("%02d : %02d", minutes, seconds));
    }

    private void showWarningMessage() {
        if (minutes == TEST_TIME_LIMIT_IN_MINUTES - 5) {
            topMessageText.setText("Внимание! До конца теста осталось 5 минут");
        }
        if (minutes == TEST_TIME_LIMIT_IN_MINUTES - 4) {
            topMessageText.setText("");
        }
        if (minutes == TEST_TIME_LIMIT_IN_MINUTES - 1) {
            topMessageText.setText("Внимание! До конца теста осталась одна минута");
        }
        if(minutes == TEST_TIME_LIMIT_IN_MINUTES) {
            main.endTest();
        }
    }

    public void startSurvey(List<Question> questions) {
        this.questions = questions;
        showQuestionByNumber(numOfQuestion);
    }

    private void showQuestionByNumber(int numOfQuestion) {
        Question question = this.questions.get(numOfQuestion);
        ObservableList<Node> children = bottomVBox.getChildren();
        children.clear();

        // показываем вопрос с картинкой
        if (question.getId() == ID_OF_SPECIAL_QUESTION) {
            specialQuestionPane = main.getSpecialQuestionPane();
            bottomPane.getChildren().remove(bottomVBox);
            bottomPane.getChildren().add(specialQuestionPane);
            // если есть ответы, отображаем их
            if (!question.getAnswers().isEmpty()) {
                main.setSpecialQuestionAnswerValue(question.getAnswers().get(0).getAnswerText());
            }
            return;
        }

        // показываем обычный вопрос
        Label questionText = new Label(question.getQuestionText());
        questionText.setMaxWidth(770);
        questionText.setWrapText(true);
        questionText.setStyle("-fx-font-size:18; -fx-font-style: italic");
        children.add(questionText);

        // показываем ответы
        for (Answer answer : question.getAnswers()) {
            CheckBox checkBox = new CheckBox(answer.getAnswerText());
            if (answer.isSelected()) {
                checkBox.setSelected(true);
            }
            checkBox.setMaxWidth(770);
            checkBox.setWrapText(true);
            checkBox.setStyle("-fx-font-size:14");
            checkBox.setTextOverrun(OverrunStyle.ELLIPSIS);
            checkBox.setOnAction(event -> {
                if (checkBox.isSelected()) {
                    answer.setSelected(true);
                } else {
                    answer.setSelected(false);
                }
            });
            children.add(checkBox);
        }
    }

    public void onNextQuestionButtonClicked(ActionEvent e) {
        Question question = this.questions.get(numOfQuestion);
        if (++numOfQuestion < questions.size()) {
            prepareLayoutAfterSpecQuestion(question);
            showQuestionByNumber(numOfQuestion);
        } else {
            main.endTest();
        }
    }

    public void onPreviousQuestionButtonClicked(ActionEvent e) {
        Question question = this.questions.get(numOfQuestion);
        if (--numOfQuestion >= 0) {
            prepareLayoutAfterSpecQuestion(question);
            showQuestionByNumber(numOfQuestion);
        } else {
            numOfQuestion = 0;
        }
    }

    private void prepareLayoutAfterSpecQuestion(Question question) {
        if (question.getId() == ID_OF_SPECIAL_QUESTION) {
            bottomPane.getChildren().remove(specialQuestionPane);
            bottomPane.getChildren().add(bottomVBox);
        }
    }

    public void onEndTestButtonClicked(ActionEvent e) {
        main.endTest();
    }

    public void onSpecialQuestionDateSelected(LocalDate value) {
        Question question = this.questions.get(numOfQuestion);
        LocalDate rightAnswer = LocalDate.of(2018, 6, 13);
        long id = value.isEqual(rightAnswer) ? 1L : 2L;
        question.setAnswers(Collections.singletonList(new Answer(id, value.toString(), true)));
    }
}
