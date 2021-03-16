package com.mshift.acf.user_services.utils;

import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

public class BaseEntity {

    @Id
    private String id;
    private LocalDateTime dateCreated;
    private LocalDateTime dateModified;

    public BaseEntity() {
        this.dateCreated = LocalDateTime.now();
        this.dateModified = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public LocalDateTime getDateModified() {
        return dateModified;
    }

    public void setDateModified(LocalDateTime dateModified) {
        this.dateModified = dateModified;
    }
}
