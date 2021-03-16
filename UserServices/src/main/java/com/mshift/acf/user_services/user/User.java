package com.mshift.acf.user_services.user;

import com.mshift.acf.user_services.utils.BaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
public class User extends BaseEntity {

    private String username;
    private String firstName;
    private String lastName;
    private String pin;

    public User() {
        super();
    }

    public User(String username, String firstName, String lastName) {
        super();
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }
}
