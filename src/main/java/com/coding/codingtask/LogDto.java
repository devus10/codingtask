package com.coding.codingtask;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
class LogDto {

    private String host;
    private String type;
    @JsonUnwrapped
    private Event event;

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    static class Event {

        private String id;
        private State state;
        private long timestamp;

        enum State {
            STARTED,
            FINISHED
        }
    }
}
