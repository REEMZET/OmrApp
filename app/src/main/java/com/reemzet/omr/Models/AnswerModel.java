package com.reemzet.omr.Models;

public class AnswerModel {
    String questionno;

    public AnswerModel(String questionno) {
        this.questionno = questionno;
    }

    public String getQuestionno() {
        return questionno;
    }

    public void setQuestionno(String questionno) {
        this.questionno = questionno;
    }
}
