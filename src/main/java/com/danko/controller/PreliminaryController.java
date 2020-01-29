package com.danko.controller;

import com.danko.Main;

public class PreliminaryController {

    private Main main;

    public void onStartTestButtonClicked() {
        main.startTest();
    }

    public void setMain(Main main) {
        this.main = main;
    }
}
