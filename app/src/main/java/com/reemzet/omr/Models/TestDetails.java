package com.reemzet.omr.Models;

public class TestDetails {

    String testname,questionno,testtime,correctmarks,wrongmarks,starttime,testcode;

    public TestDetails() {

    }

    public TestDetails(String testname, String questionno, String testtime, String correctmarks, String wrongmarks, String starttime, String testcode) {
        this.testname = testname;
        this.questionno = questionno;
        this.testtime = testtime;
        this.correctmarks = correctmarks;
        this.wrongmarks = wrongmarks;
        this.starttime = starttime;
        this.testcode = testcode;
    }

    public String getTestname() {
        return testname;
    }

    public void setTestname(String testname) {
        this.testname = testname;
    }

    public String getQuestionno() {
        return questionno;
    }

    public void setQuestionno(String questionno) {
        this.questionno = questionno;
    }

    public String getTesttime() {
        return testtime;
    }

    public void setTesttime(String testtime) {
        this.testtime = testtime;
    }

    public String getCorrectmarks() {
        return correctmarks;
    }

    public void setCorrectmarks(String correctmarks) {
        this.correctmarks = correctmarks;
    }

    public String getWrongmarks() {
        return wrongmarks;
    }

    public void setWrongmarks(String wrongmarks) {
        this.wrongmarks = wrongmarks;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public String getTestcode() {
        return testcode;
    }

    public void setTestcode(String testcode) {
        this.testcode = testcode;
    }
}
