package com.reemzet.omr.Models;

import java.util.ArrayList;

public class OmrModel {
    String questionno,answer;

    public OmrModel(String questionno, String answer) {
        this.questionno = questionno;
        this.answer = answer;
    }

    public String getQuestionno() {
        return questionno;
    }

    public void setQuestionno(String questionno) {
        this.questionno = questionno;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}