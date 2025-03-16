package io.extact.msa.spring.platform.fw.stub.client.converter;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange("/convert")
public interface ConverterClientApi {

    @GetExchange("/date/{date}")
    LocalDate pathLocalDate(@PathVariable("date") LocalDate date);

    @GetExchange("/datetime/{datetime}")
    LocalDateTime pathLocalDateTime(@PathVariable("datetime") LocalDateTime dateTime);

    @GetExchange("/date")
    LocalDate paramlocalDate(@RequestParam("date") LocalDate date);

    @GetExchange("/datetime")
    LocalDateTime paramLocalDateTime(@RequestParam("datetime") LocalDateTime dateTime);

    @PostExchange
    DateDto dateDto(@RequestBody DateDto dto);

    static record DateDto(
            LocalDate date,
            LocalDateTime dateTime) {
    }

}
