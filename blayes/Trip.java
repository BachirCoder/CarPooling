package com.bachir.owner.blayes;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

/**
 * Created by Owner on 5/24/2016.
 */
public class Trip {

    String id;
    String driver = "Anonyme";
    String userid = "unknown@blayes.com";
    String destination;
    String departure;
    Integer seat;
    Integer price;
    Integer booked;
    Integer pending;
    HashMap<String, HashMap<String, String>> book_list;
    HashMap<String, HashMap<String, String>> pen_list;
    GregorianCalendar datetime;
    Date stamp;
    String contact;
    String details;
    String token;

    static HashMap<String, HashMap<String, String>> book_l;
    static HashMap<String, HashMap<String, String>> pen_l;

    public Trip(){
    };

    public Trip(String userid, String driver, String departure, String destination, GregorianCalendar datetime, Integer seat, Integer price, String contact, String details, String token){
        this.stamp = new Date();
        this.userid = userid;
        this.driver = driver;
        this.destination = destination;
        this.departure = departure;
        this.seat = seat;
        this.price = price;
        this.pending = 0;
        this.booked = 0;
        this.book_list = new HashMap<>();
        this.pen_list = new HashMap<>();
        this.datetime = datetime;
        this.contact = contact;
        this.details = details;
        this.token = token;
    };



    public final static String gToString(GregorianCalendar var, int i){
        SimpleDateFormat dateformat;
        dateformat= new SimpleDateFormat("dd/MM/yyyy");
        switch (i) {
            case 1: dateformat= new SimpleDateFormat("dd/MM/yyyy HH:mm");
                break;
            case 2: dateformat= new SimpleDateFormat("dd/MM/yyyy");
                break;
            case 3: dateformat= new SimpleDateFormat("HH:mm");
                break;
        }
        dateformat.setCalendar(var);
        String datetime = dateformat.format(var.getTime());
        return datetime;
    }


    public void setId(String id) { this.id = id; }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public void setUserid(String userid) {this.userid = userid;}

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    public void setSeat(Integer seat) {
        this.seat = seat;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public void setBooked(Integer booked) {
        this.booked = booked;
    }

    public void booked_append(Integer booked) { this.booked += booked; }

    public void setPending(Integer pending) { this.pending = pending; }

    public void pen_append(Integer pen) { this.pending += pen; }

    public void addBook_list(String key, HashMap<String, String> value) { this.book_list.put(key, value); }

    public void addPen_list(String key, HashMap<String, String> value) { this.pen_list.put(key, value); }

    public void remPen_list(String key) { this.pen_list.remove(key); }

    public void remBook_list(String key) { this.book_list.remove(key); }

    public void setBook_list(HashMap<String, HashMap<String, String>> book_list) { this.book_list = book_list; }

    public void setPen_list(HashMap<String, HashMap<String, String>> pen_list) { this.pen_list = pen_list; }

    public void setDatetime(GregorianCalendar datetime) {
        this.datetime = datetime;
    }

    public void setDetails(String details) { this.details = details; }

    public void setContact(String contact) { this.contact = contact; }

    public void setStamp(Date stamp) { this.stamp = stamp; }

    public void setToken(String token) { this.token = token; }

    public String getId() {
        return id;
    }

    public String getDriver() {
        return driver;
    }

    public String getUserid() { return userid; }

    public String getDestination() {
        return destination;
    }

    public String getDeparture() { return departure; }

    public Integer getSeat() { return seat; }

    public Integer getPrice() { return price; }

    public Integer getBooked() { return booked; }

    public Integer getPending() { return pending; }

    public HashMap<String, HashMap<String, String>> getBook_list() { return book_list; }

    public HashMap<String, HashMap<String, String>> getPen_list() { return pen_list; }

    public GregorianCalendar getDatetime() {
        return datetime;
    }

    public String getContact() {
        return contact;
    }

    public String getDetails() {
        return details;
    }

    public Date getStamp() {
        return stamp;
    }

    public String getToken() {
        return token;
    }
}
