package com.danko.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import com.danko.Main;

public class ResultController {

    @FXML
    private Label resultText;

    private Main main;

    public void setMain(Main main) {
        this.main = main;
    }

    public void showResult(double points) {
        resultText.setText(String.format("%.2f", points));
    }
}
