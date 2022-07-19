package com.reemzet.omr.Models;

public class ScoreModel {
    String totalmarks,correctquestion,unattempedquestion,totalquestion,totaltime;

    public ScoreModel() {
    }

    public ScoreModel(String totalmarks, String correctquestion, String unattempedquestion, String totalquestion, String totaltime) {
        this.totalmarks = totalmarks;
        this.correctquestion = correctquestion;
        this.unattempedquestion = unattempedquestion;
        this.totalquestion = totalquestion;
        this.totaltime = totaltime;
    }

    public String getTotalmarks() {
        return totalmarks;
    }

    public void setTotalmarks(String totalmarks) {
        this.totalmarks = totalmarks;
    }

    public String getCorrectquestion() {
        return correctquestion;
    }

    public void setCorrectquestion(String correctquestion) {
        this.correctquestion = correctquestion;
    }

    public String getUnattempedquestion() {
        return unattempedquestion;
    }

    public void setUnattempedquestion(String unattempedquestion) {
        this.unattempedquestion = unattempedquestion;
    }

    public String getTotalquestion() {
        return totalquestion;
    }

    public void setTotalquestion(String totalquestion) {
        this.totalquestion = totalquestion;
    }

    public String getTotaltime() {
        return totaltime;
    }

    public void setTotaltime(String totaltime) {
        this.totaltime = totaltime;
    }
}
