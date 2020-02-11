package com.danko.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import com.danko.Main;
import javafx.scene.control.Label;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.Month;

public class SpecialQuestionViewController {

    private static final String APRIL_MONTH = "апрель";
    private static final String MAY_MONTH = "май";
    private static final String JUNE_MONTH = "июнь";

    @FXML
    private ComboBox<Integer> dayPicker;

    @FXML
    private ComboBox<String> monthPicker;

    @FXML
    private Label errorMessage;

    private LocalDate dateResult = LocalDate.of(2018, 1, 1);

    private Main main;

    public void setMain(Main main) {
        this.main = main;
        initController();
    }

    private void initController() {
        errorMessage.setWrapText(true);
        errorMessage.setMaxWidth(150);
        for (int i = 1; i < 32; i++) {
            dayPicker.getItems().add(i);
        }
        monthPicker.getItems().add(APRIL_MONTH);
        monthPicker.getItems().add(MAY_MONTH);
        monthPicker.getItems().add(JUNE_MONTH);
    }

    public void onDaySelected(ActionEvent e) {
        try {
            dateResult = LocalDate.of(dateResult.getYear(), dateResult.getMonth(), dayPicker.getValue());
        } catch (Exception e1) {
            errorMessage.setText("Неверная дата. Попробуйте другой вариант");
        }
        main.onSpecialQuestionDateSelected(dateResult);
    }

    public void onMonthSelected(ActionEvent e) {
        Month month;
        switch (monthPicker.getValue()) {
            case APRIL_MONTH: month = Month.APRIL; break;
            case MAY_MONTH: month = Month.MAY; break;
            case JUNE_MONTH: month = Month.JUNE; break;
            default: month = Month.JANUARY;
        }
        try {
            dateResult = LocalDate.of(dateResult.getYear(), month, dateResult.getDayOfMonth());
        } catch (DateTimeException e1) {
            errorMessage.setText("Неверная дата. Попробуйте другой вариант");
        }
        main.onSpecialQuestionDateSelected(dateResult);
    }

    public void setSpecialQuestionAnswerValue(String answerText) {
        dateResult = LocalDate.parse(answerText);
        dayPicker.setValue(dateResult.getDayOfMonth());
        String month;
        switch (dateResult.getMonth()) {
            case APRIL: month = APRIL_MONTH; break;
            case MAY: month = MAY_MONTH; break;
            case JUNE: month = JUNE_MONTH; break;
            default: month = APRIL_MONTH;
        }
        monthPicker.setValue(month);
    }
}
