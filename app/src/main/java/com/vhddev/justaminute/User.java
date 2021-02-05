package com.vhddev.justaminute;

public class User
{
    private int uid;
    private String uname,ugender,umobile,upass;

    public User(int uid, String uname, String ugender, String umobile, String upass) {
        this.uid = uid;
        this.uname = uname;
        this.ugender = ugender;
        this.umobile = umobile;
        this.upass = upass;
    }

    public int getUid() {
        return uid;
    }

    public String getUname() {
        return uname;
    }

    public String getUgender() {
        return ugender;
    }

    public String getUmobile() {
        return umobile;
    }

    public String getUpass() {
        return upass;
    }
}
