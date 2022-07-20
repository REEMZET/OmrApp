package com.reemzet.omr.Models;

public class RankModel {
        String studentname,studentuid,studentimg,totaltime;
        int totalmarks;
    public RankModel() {
    }

    public RankModel(String studentname, String studentuid, String studentimg,String totaltime, int totalmarks) {
        this.studentname = studentname;
        this.studentuid = studentuid;
        this.studentimg = studentimg;

        this.totaltime = totaltime;
        this.totalmarks = totalmarks;
    }

    public String getStudentname() {
        return studentname;
    }

    public void setStudentname(String studentname) {
        this.studentname = studentname;
    }

    public String getStudentuid() {
        return studentuid;
    }

    public void setStudentuid(String studentuid) {
        this.studentuid = studentuid;
    }

    public String getStudentimg() {
        return studentimg;
    }

    public void setStudentimg(String studentimg) {
        this.studentimg = studentimg;
    }

    public String getTotaltime() {
        return totaltime;
    }

    public void setTotaltime(String totaltime) {
        this.totaltime = totaltime;
    }

    public int getTotalmarks() {
        return totalmarks;
    }

    public void setTotalmarks(int totalmarks) {
        this.totalmarks = totalmarks;
    }
}
