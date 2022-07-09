package com.reemzet.omr.Models;

public class InstuteDetails {
  String  teachername,institutename,orgcode,city,teacheremail,teacherphone,accounttype;

    public InstuteDetails() {
    }

    public InstuteDetails(String teachername, String institutename, String orgcode, String city, String teacheremail, String teacherphone, String accounttype) {
        this.teachername = teachername;
        this.institutename = institutename;
        this.orgcode = orgcode;
        this.city = city;
        this.teacheremail = teacheremail;
        this.teacherphone = teacherphone;
        this.accounttype = accounttype;
    }



    public String getTeachername() {
        return teachername;
    }

    public void setTeachername(String teachername) {
        this.teachername = teachername;
    }

    public String getInstitutename() {
        return institutename;
    }

    public void setInstitutename(String institutename) {
        this.institutename = institutename;
    }

    public String getOrgcode() {
        return orgcode;
    }

    public void setOrgcode(String orgcode) {
        this.orgcode = orgcode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getTeacheremail() {
        return teacheremail;
    }

    public void setTeacheremail(String teacheremail) {
        this.teacheremail = teacheremail;
    }

    public String getTeacherphone() {
        return teacherphone;
    }

    public void setTeacherphone(String teacherphone) {
        this.teacherphone = teacherphone;
    }

    public String getAccounttype() {
        return accounttype;
    }

    public void setAccounttype(String accounttype) {
        this.accounttype = accounttype;
    }
}
