package com.bachir.owner.blayes;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

/**
 * Created by Owner on 6/11/2017.
 */

public class Message {

    public String subject="";
    public String text="";
    public String uid="";
    public String name="";
    public String email="";
    public String Timestamp="";


    public Message(String subject, String text, String uid, String name, String email) {
        this.subject = subject;
        this.text = text;
        this.uid = uid;
        this.name = name;
        this.email = email;

        GregorianCalendar date = new GregorianCalendar();
        SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        dateformat.setCalendar(date);
        String datetime = dateformat.format(date.getTime());
        System.out.println("Current Date Time : " + datetime);
        this.Timestamp = datetime;
    }

}
