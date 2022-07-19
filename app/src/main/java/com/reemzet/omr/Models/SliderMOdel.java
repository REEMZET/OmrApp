package com.reemzet.omr.Models;

public class SliderMOdel {
    String title,message,bgcard,titlecolor,messagecolor;

    public SliderMOdel() {
    }

    public SliderMOdel(String title, String message, String bgcard, String titlecolor, String messagecolor) {
        this.title = title;
        this.message = message;
        this.bgcard = bgcard;
        this.titlecolor = titlecolor;
        this.messagecolor = messagecolor;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getBgcard() {
        return bgcard;
    }

    public void setBgcard(String bgcard) {
        this.bgcard = bgcard;
    }

    public String getTitlecolor() {
        return titlecolor;
    }

    public void setTitlecolor(String titlecolor) {
        this.titlecolor = titlecolor;
    }

    public String getMessagecolor() {
        return messagecolor;
    }

    public void setMessagecolor(String messagecolor) {
        this.messagecolor = messagecolor;
    }
}
