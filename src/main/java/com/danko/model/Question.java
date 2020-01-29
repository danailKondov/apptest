package com.danko.model;

import java.util.List;

public class Question {

    private Long id;
    private String questionText;
    private List<Long> rightAnswersIds;
    private List<Answer> answers;
    private double score;

    public Question(Long id, String questionText, List<Long> rightAnswersIds, List<Answer> answers) {
        this.id = id;
        this.questionText = questionText;
        this.rightAnswersIds = rightAnswersIds;
        this.answers = answers;
    }

    public Long getId() {
        return id;
    }

    public String getQuestionText() {
        return questionText;
    }

    public List<Long> getRightAnswersIds() {
        return rightAnswersIds;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    public void setScore(double score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", rightAnswersIds=" + rightAnswersIds +
                ", answers=" + answers +
                ", score=" + score +
                '}';
    }
}
