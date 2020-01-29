package com.danko.util;

import com.danko.model.Answer;
import com.danko.model.Question;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Util {

    public static List<Question> initQuestions() {
        List<Question> questions = new ArrayList<>();
        Question question = new Question(0L,
                "Назовите число Пи?",
                Collections.singletonList(1L),
                Arrays.asList(new Answer(1L, "3,14..."), new Answer(2L, "3,15..."), new Answer(3L, "2,14...")));
        Question question2 = new Question(1L,
                "Самый лучший город на Земле?",
                Collections.singletonList(2L),
                Arrays.asList(new Answer(1L, "Берлин"), new Answer(2L, "Москва"), new Answer(3L, "Лондон")));
        questions.add(question);
        questions.add(question2);
        return questions;
    }
}
