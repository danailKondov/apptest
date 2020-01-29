package com.danko.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import com.danko.Main;

import java.time.LocalDate;

public class SpecialQuestionViewController {

    @FXML
    private DatePicker datePicker;

    private Main main;

    public void setMain(Main main) {
        this.main = main;
    }

    public void onDataPicked(ActionEvent e) {
        LocalDate value = datePicker.getValue();
        main.onSpecialQuestionDateSelected(value);
        datePicker.setValue(value);
    }

    public void setSpecialQuestionAnswerValue(String answerText) {
        datePicker.setValue(LocalDate.parse(answerText));
    }
}
