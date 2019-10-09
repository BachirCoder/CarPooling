package com.bachir.owner.blayes;

import java.util.HashMap;

/**
 * Created by Owner on 1/24/2017.
 */
public class User {
    private String name;
    private String email;
    private String token;
    private Boolean allowPost = true;
    private Boolean allowBook = true;
    private HashMap<String, Object> timestampJoined;
    private boolean log;

    /**
     * Required public constructor
     */
    public User() {
    }

    /**
     * Use this constructor to create new User.
     * Takes user name, email and timestampJoined as params
     *
     * @param name
     * @param email
     * @param timestampJoined
     */
    public User(String name, String email, HashMap<String, Object> timestampJoined, String token) {
        this.name = name;
        this.email = email;
        this.timestampJoined = timestampJoined;
        this.log = false;
        this.token = token;
    }

    public String getName() {
        if (name == null) return "Anonyme";
        else return name;
    }

    public String getToken() {
        return token;
    }

    public String getEmail() {
        return email;
    }

    public Boolean getAllowPost() { return allowPost;}

    public Boolean getAllowBook() { return allowBook;}

    public boolean getLog() { return log; }

    public HashMap<String, Object> getTimestampJoined() { return timestampJoined;}

}
