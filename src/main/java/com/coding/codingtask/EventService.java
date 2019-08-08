package com.coding.codingtask;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.coding.codingtask.LogDto.Event.State.FINISHED;
import static com.coding.codingtask.LogDto.Event.State.STARTED;

@Component
@RequiredArgsConstructor
@Slf4j
class EventService {

    private final JsonLogReader jsonLogReader;
    private final EventRepository eventRepository;

    void saveEventsFromLogFile(String filePath) {
        List<Event> events = new ArrayList<>();
        jsonLogReader.read(filePath).stream()
                .collect(Collectors.groupingBy(log -> log.getEvent().getId(), Collectors.toList()))
                .forEach((eventId, logs) -> events.add(createEventFromLog(eventId, logs)));

        if (!events.isEmpty()) {
            eventRepository.saveAll(events);
            log.info("Saved events: {}", events.toString());
            return;
        }

        log.info("No events to save");
    }

    private Event createEventFromLog(String id, List<LogDto> logs) {
        if (logs.size() != 2) {
            throw new IllegalStateException("There must be 2 log entries for one event");
        }

        LogDto firstLog = firstElementOf(logs);
        return new Event(
                id,
                calculateEventDuration(toEvents(logs)),
                firstLog.getType(),
                firstLog.getHost()
        );
    }

    private List<LogDto.Event> toEvents(List<LogDto> logs) {
        return logs.stream().map(LogDto::getEvent).collect(Collectors.toList());
    }

    private LogDto firstElementOf(List<LogDto> logs) {
        return logs.stream().findFirst().orElseThrow(() -> new IllegalStateException("First element not found"));
    }

    private long calculateEventDuration(List<LogDto.Event> events) {
        LogDto.Event startingEvent = findEventByState(events, STARTED);
        LogDto.Event finishingEvent = findEventByState(events, FINISHED);

        return finishingEvent.getTimestamp() - startingEvent.getTimestamp();
    }

    private LogDto.Event findEventByState(List<LogDto.Event> events, LogDto.Event.State state) {
        return events.stream()
                .filter(event -> event.getState() == state)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(String.format("No event with state %s found", state)));
    }
}
