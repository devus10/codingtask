package com.coding.codingtask

import spock.lang.Specification

import static com.coding.codingtask.LogDto.Event.State.FINISHED
import static com.coding.codingtask.LogDto.Event.State.STARTED

class EventServiceUT extends Specification {

    JsonLogReader jsonLogReader = Mock()
    EventRepository eventRepository = Mock()
    def eventService = new EventService(jsonLogReader, eventRepository)

    def "should save events"() {
        given: 'file path'
            def filePath = 'path'

        and: 'logs from file'
            def logs = [
                    new LogDto(null, null, new LogDto.Event('1', FINISHED, 200)),
                    new LogDto('123', 'type2', new LogDto.Event('2', STARTED, 100)),
                    new LogDto('123', 'type2', new LogDto.Event('2', FINISHED, 150)),
                    new LogDto(null, null, new LogDto.Event('1', STARTED, 199))
            ]
            jsonLogReader.read(filePath) >> logs

        when: 'saving events retrieved from logs'
            eventService.saveEventsFromLogFile(filePath)

        then: 'correct events are saved'
            1 * eventRepository.saveAll(_ as List<Event>) >> { args ->
                def savedEvents = args[0] as List<Event>
                assert savedEvents.size() == 2

                assert savedEvents.any {
                    it.eventId == '1' &&
                            it.duration == 1 &&
                            it.type == null &&
                            it.host == null &&
                            !it.alert
                }

                assert savedEvents.any {
                    it.eventId == '2' &&
                            it.duration == 50 &&
                            it.type == 'type2' &&
                            it.host == '123' &&
                            it.alert
                }
            }
    }

    def "should fail to save events when there are no 2 entries for one event"() {
        given: 'file path'
            def filePath = 'path'

        and: 'logs from file'
            def logs = [
                    new LogDto(null, null, new LogDto.Event('1', FINISHED, 200)),
            ]
            jsonLogReader.read(filePath) >> logs

        when: 'saving events retrieved from logs'
            eventService.saveEventsFromLogFile(filePath)

        then: 'exception is thrown'
            def ex = thrown(IllegalStateException)
            ex.message == 'There must be 2 log entries for one event'
    }

    def "should fail to save events when event with required state was not found"() {
        given: 'file path'
            def filePath = 'path'

        and: 'logs from file'
            def logs = [
                    new LogDto(null, null, new LogDto.Event('1', STARTED, 200)),
                    new LogDto(null, null, new LogDto.Event('1', null, 150)),
            ]
            jsonLogReader.read(filePath) >> logs

        when: 'saving events retrieved from logs'
            eventService.saveEventsFromLogFile(filePath)

        then: 'exception is thrown'
            def ex = thrown(IllegalStateException)
            ex.message == 'No event with state FINISHED found'
    }

    def "should not save events when there are no logs"() {
        given: 'file path'
            def filePath = 'path'

        and: 'logs from file'
            jsonLogReader.read(filePath) >> []

        when: 'saving events retrieved from logs'
            eventService.saveEventsFromLogFile(filePath)

        then: 'no events have been saved'
            0 * eventRepository.saveAll(_)
    }
}
