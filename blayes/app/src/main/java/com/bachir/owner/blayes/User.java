package com.bachir.owner.blayes;

import java.util.HashMap;

/**
 * Created by Owner on 1/24/2017.
 */
public class User {
    private String name;
    private String phone;
    private String email;
    private String token;
    private boolean allowPost = true;
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
    public User(String name, String phone, String email, HashMap<String, Object> timestampJoined, String token) {
        this.name = name;
        this.phone = phone;
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

    public String getPhone() { return phone;}

    public boolean getLog() { return log; }

    public boolean getAllowPost() { return allowPost; }

    public HashMap<String, Object> getTimestampJoined() { return timestampJoined;}

}
