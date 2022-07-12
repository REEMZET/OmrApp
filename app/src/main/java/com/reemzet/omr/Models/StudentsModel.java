package com.reemzet.omr.Models;

public class StudentsModel {
  String  studenname,studentphone,studentcity,studentemail,preparation,batch;

    public StudentsModel() {
    }

    public StudentsModel(String studenname, String studentphone, String studentcity, String studentemail, String preparation, String batch) {
        this.studenname = studenname;
        this.studentphone = studentphone;
        this.studentcity = studentcity;
        this.studentemail = studentemail;
        this.preparation = preparation;
        this.batch = batch;
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
}
