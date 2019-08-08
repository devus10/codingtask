package com.coding.codingtask;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
class JsonLogReader {

    private final ObjectMapper objectMapper;

    List<LogDto> read(String filePath) {
        List<LogDto> logs = new ArrayList<>();
        try (Stream<String> stream = Files.lines(Paths.get(filePath))) {
            stream.forEach(line -> logs.add(toLog(line)));
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read a file", e);
        }

        return logs;
    }

    private LogDto toLog(String jsonLog) {
        try {
            return objectMapper.readValue(jsonLog, LogDto.class);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to map JSON log to object log", e);
        }
    }
}
