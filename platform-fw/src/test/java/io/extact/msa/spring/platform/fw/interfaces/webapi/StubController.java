package io.extact.msa.spring.platform.fw.interfaces.webapi;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@ApiController("/convert")
public class StubController {

    @GetMapping("/date/{date}")
    public String pathLocalDate(
            @PathVariable("date") LocalDate date,
            @PathVariable("date") String original) {

        return original;
    }

    @GetMapping("/date/pattern/{date}")
    public String pathLocalDateWithPattern(
            @PathVariable("date") @DateTimeFormat(pattern = "yyyy.MM.dd") LocalDate date,
            @PathVariable("date") String original) {

        return original;
    }

    @GetMapping("/datetime/{datetime}")
    public String pathLocalDateTime(
            @PathVariable("datetime") LocalDateTime dateTime,
            @PathVariable("datetime") String original) {

        return original;
    }

    @GetMapping("/datetime/pattern/{datetime}")
    public String pathLocalDateTimeWithPattern(
            @PathVariable("datetime") @DateTimeFormat(pattern = "yyyy.MM.dd HH:mm") LocalDateTime dateTime,
            @PathVariable("datetime") String original) {

        return original;
    }

    @GetMapping("/date")
    public String paramlocalDate(
            @RequestParam("date") LocalDate date,
            @RequestParam("date") String original) {

        return original;
    }

    @GetMapping("/date/pattern")
    public String paramlocalDateWithPattern(
            @RequestParam("date") @DateTimeFormat(pattern = "yyyy.MM.dd") LocalDate date,
            @RequestParam("date") String original) {

        return original;
    }

    @GetMapping("/datetime")
    public String paramLocalDateTime(
            @RequestParam("datetime") LocalDateTime dateTime,
            @RequestParam("datetime") String original) {
        return original;
    }

    @GetMapping("/datetime/pattern")
    public String paramLocalDateTimeWithPattern(
            @RequestParam("datetime") @DateTimeFormat(pattern = "yyyy.MM.dd HH:mm") LocalDateTime dateTime,
            @RequestParam("datetime") String original) {
        return original;
    }

    @PostMapping
    public DateDto dateDto(@RequestBody DateDto dto) {
        return dto;
    }

    static record DateDto(
            LocalDate date,
            LocalDateTime dateTime) {
    }

}
