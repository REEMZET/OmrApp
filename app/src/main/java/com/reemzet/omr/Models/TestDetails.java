package com.reemzet.omr.Models;

public class TestDetails {

    String testname,questionno,testtime,correctmarks,wrongmarks,starttime,testcode,testdate,status,testid,resultstatus;


    public TestDetails() {

    }

    public TestDetails(String testname, String questionno, String testtime, String correctmarks, String wrongmarks, String starttime, String testcode, String testdate, String status, String testid, String resultstatus) {
        this.testname = testname;
        this.questionno = questionno;
        this.testtime = testtime;
        this.correctmarks = correctmarks;
        this.wrongmarks = wrongmarks;
        this.starttime = starttime;
        this.testcode = testcode;
        this.testdate = testdate;
        this.status = status;
        this.testid = testid;
        this.resultstatus = resultstatus;
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

    public String getTestdate() {
        return testdate;
    }

    public void setTestdate(String testdate) {
        this.testdate = testdate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTestid() {
        return testid;
    }

    public void setTestid(String testid) {
        this.testid = testid;
    }

    public String getResultstatus() {
        return resultstatus;
    }

    public void setResultstatus(String resultstatus) {
        this.resultstatus = resultstatus;
    }
}
