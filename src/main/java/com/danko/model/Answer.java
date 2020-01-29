package com.danko.model;

public class Answer {

    private final Long id;
    private String answerText;
    private boolean selected;

    public Answer(Long id, String answerText) {
        this.id = id;
        this.answerText = answerText;
    }

    public Answer(Long id, String answerText, boolean selected) {
        this.id = id;
        this.answerText = answerText;
        this.selected = selected;
    }

    public Long getId() {
        return id;
    }

    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Answer answer = (Answer) o;

        if (getId() != null ? !getId().equals(answer.getId()) : answer.getId() != null) return false;
        return getAnswerText() != null ? getAnswerText().equals(answer.getAnswerText()) : answer.getAnswerText() == null;
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getAnswerText() != null ? getAnswerText().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Answer{" +
                "id=" + id +
                ", answerText='" + answerText + '\'' +
                '}';
    }
}
