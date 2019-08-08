package com.coding.codingtask

import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Specification

class JsonLogReaderUT extends Specification {

    static LOG_FILE = 'example.log'

    ObjectMapper objectMapper = Mock()
    def jsonLogReader = new JsonLogReader(objectMapper)

    def "should read from file and return logs"() {
        given: 'log file path'
            def filePath = filePath()

        and: 'prepared object mapper'
            def log = Mock(LogDto)
            objectMapper.readValue(_ as String, LogDto.class) >> log

        when: 'reading logs from file'
            def logs = jsonLogReader.read(filePath)

        then: 'number of logs corresponds to number of file lines'
            logs.size() == 6

        and: 'logs are mapped to objects correctly'
            logs.every { it == log }
    }

    def "should fail when file does not exist"() {
        given: 'non-existing file path'
            def filePath = 'non-existing'

        when: 'reading logs from file'
            jsonLogReader.read(filePath)

        then: 'exception is thrown'
            def ex = thrown(IllegalStateException)
            ex.message == 'Cannot read a file'
    }

    def "should fail when error occurred during mapping to object"() {
        given: 'log file path'
            def filePath = filePath()

        and: 'prepared object mapper'
            objectMapper.readValue(_ as String, LogDto.class) >> { throw new IOException('reason')}

        when: 'reading logs from file'
            jsonLogReader.read(filePath)

        then: 'exception is thrown'
            def ex = thrown(IllegalStateException)
            ex.message == 'Unable to map JSON log to object log'
    }

    def filePath() {
        new File(getClass().getClassLoader().getResource(LOG_FILE).getFile()).getAbsolutePath()
    }

}
