package com.reemzet.omr.Models;

public class OmrModel {
    String a,b,c,d,questionno;

    public OmrModel(String a, String b, String c, String d, String questionno) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.questionno = questionno;
    }

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

    public String getB() {
        return b;
    }

    public void setB(String b) {
        this.b = b;
    }

    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }

    public String getD() {
        return d;
    }

    public void setD(String d) {
        this.d = d;
    }

    public String getQuestionno() {
        return questionno;
    }

    public void setQuestionno(String questionno) {
        this.questionno = questionno;
    }
}
