package io.extact.msa.spring.platform.fw.infrastructure.external.converter;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import io.extact.msa.spring.platform.fw.interfaces.webapi.ApiController;

@ApiController("/converter")
public class ConverterClientApiController {

    @GetMapping("/date/{date}")
    public String pathLocalDate(@PathVariable("date") String value) {
        return value;
    }

    @GetMapping("/datetime/{datetime}")
    public String pathLocalDateTime(@PathVariable("datetime") String value) {
        return value;
    }

    @GetMapping("/date")
    public String paramlocalDate(@RequestParam("date") String value) {
        return value;
    }

    @GetMapping("/datetime")
    public String paramLocalDateTime(@RequestParam("datetime") String value) {
        return value;
    }

    @GetMapping(path = "/return/date", produces = MediaType.APPLICATION_JSON_VALUE)
    String returnLocalDate(@RequestParam("date") String date) {
        return "\"" + date + "\""; // JSONデータとするため明示的に"で囲んでいる
    }

    @GetMapping(path = "/return/datetime", produces = MediaType.APPLICATION_JSON_VALUE)
    String returnLocalDateTime(@RequestParam("datetime") String dateTime) {
        return "\"" + dateTime + "\""; // JSONデータとするため明示的に"で囲んでいる
    }

    @PostMapping("/serialize")
    TestStringDto serializeDto(@RequestBody TestStringDto dto) {
        return dto;
    }

    @PostMapping("/deserialize")
    TestStringDto deserializeDto(@RequestBody TestStringDto dto) {
        return dto;
    }

    static record TestStringDto(
            String date,
            String dateTime) {
    }
}
