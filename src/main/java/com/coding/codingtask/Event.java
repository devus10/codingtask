package com.coding.codingtask;

import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@ToString
class Event {

    private static long DURATION_TO_ALERT_IN_MILLIS = 4;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String eventId;
    private long duration;
    private String type;
    private String host;
    private boolean alert;

    Event(String eventId, long duration, String type, String host) {
        this.eventId = eventId;
        this.duration = duration;
        this.type = type;
        this.host = host;
        setAlert();
    }

    private void setAlert() {
        if (duration > DURATION_TO_ALERT_IN_MILLIS) {
            alert = true;
        }
    }
}
