package com.reemzet.omr.Models;

public class StudentsModel {
    String studenname, studentphone, studentcity, studentstates, studentemail,preparation,batch,studentuid,requestedbatch,imageurl,totaltest,totalmarksobtained,maximumtestmarks;

    public StudentsModel() {
    }

    public StudentsModel(String studenname, String studentphone, String studentcity, String studentstates, String studentemail, String preparation, String batch, String studentuid, String requestedbatch, String imageurl, String totaltest, String totalmarksobtained, String maximumtestmarks) {
        this.studenname = studenname;
        this.studentphone = studentphone;
        this.studentcity = studentcity;
        this.studentstates = studentstates;
        this.studentemail = studentemail;
        this.preparation = preparation;
        this.batch = batch;
        this.studentuid = studentuid;
        this.requestedbatch = requestedbatch;
        this.imageurl = imageurl;
        this.totaltest = totaltest;
        this.totalmarksobtained = totalmarksobtained;
        this.maximumtestmarks = maximumtestmarks;
    }

    public String getStudenname() {
        return studenname;
    }

    public void setStudenname(String studenname) {
        this.studenname = studenname;
    }

    public String getStudentphone() {
        return studentphone;
    }

    public void setStudentphone(String studentphone) {
        this.studentphone = studentphone;
    }

    public String getStudentcity() {
        return studentcity;
    }

    public void setStudentcity(String studentcity) {
        this.studentcity = studentcity;
    }

    public String getStudentstates() {
        return studentstates;
    }

    public void setStudentstates(String studentstates) {
        this.studentstates = studentstates;
    }

    public String getStudentemail() {
        return studentemail;
    }

    public void setStudentemail(String studentemail) {
        this.studentemail = studentemail;
    }

    public String getPreparation() {
        return preparation;
    }

    public void setPreparation(String preparation) {
        this.preparation = preparation;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getStudentuid() {
        return studentuid;
    }

    public void setStudentuid(String studentuid) {
        this.studentuid = studentuid;
    }

    public String getRequestedbatch() {
        return requestedbatch;
    }

    public void setRequestedbatch(String requestedbatch) {
        this.requestedbatch = requestedbatch;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getTotaltest() {
        return totaltest;
    }

    public void setTotaltest(String totaltest) {
        this.totaltest = totaltest;
    }

    public String getTotalmarksobtained() {
        return totalmarksobtained;
    }

    public void setTotalmarksobtained(String totalmarksobtained) {
        this.totalmarksobtained = totalmarksobtained;
    }

    public String getMaximumtestmarks() {
        return maximumtestmarks;
    }

    public void setMaximumtestmarks(String maximumtestmarks) {
        this.maximumtestmarks = maximumtestmarks;
    }
}
