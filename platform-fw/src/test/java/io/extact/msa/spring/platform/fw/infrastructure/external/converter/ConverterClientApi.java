package io.extact.msa.spring.platform.fw.infrastructure.external.converter;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange("/converter")
public interface ConverterClientApi {

    @GetExchange("/date/{date}")
    String pathLocalDate(@PathVariable LocalDate date);

    @GetExchange("/datetime/{datetime}")
    String pathLocalDateTime(@PathVariable LocalDateTime datetime);

    @GetExchange("/date")
    String paramLocalDate(@RequestParam LocalDate date);

    @GetExchange("/datetime")
    String paramLocalDateTime(@RequestParam LocalDateTime datetime);

    @GetExchange("/return/date")
    LocalDate returnLocalDate(@RequestParam String date);

    @GetExchange("/return/datetime")
    LocalDateTime returnLocalDateTime(@RequestParam String datetime);

    @PostExchange("/serialize")
    StringTypeDto serializeDto(@RequestBody DateTypeDto dto);

    @PostExchange("/deserialize")
    DateTypeDto deserializeDto(@RequestBody StringTypeDto dto);

    static record DateTypeDto(
            LocalDate date,
            LocalDateTime dateTime) {
    }

    static record StringTypeDto(
            String date,
            String dateTime) {
    }
}
