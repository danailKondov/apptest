package com.danko.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import com.danko.Main;


public class MainController {

    private Main main;

    @FXML
    private TextField lastNameTextArea;

    @FXML
    private TextField firstNameTextArea;

    @FXML
    private Label errorMessage;

    public void startTestButtonPressed(ActionEvent e) {
        errorMessage.setText("");
        String lastName = lastNameTextArea.getText();
        String firstName = firstNameTextArea.getText();
        if (firstName != null && !firstName.trim().isEmpty()
                && lastName != null && !lastName.trim().isEmpty()) {
            main.setFirstName(firstName);
            main.setLastName(lastName);
            main.startPreliminaryPage();
        } else {
            errorMessage.setText("Необходимо ввести имя и фамилию!");
        }
    }

    public void setMain(Main main) {
        this.main = main;
    }
}
