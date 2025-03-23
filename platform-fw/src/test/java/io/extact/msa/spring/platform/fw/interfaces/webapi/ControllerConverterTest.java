package io.extact.msa.spring.platform.fw.interfaces.webapi;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest
class ControllerConverterTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;

    @Configuration(proxyBeanMethods = false)
    @Import(RestControllerConfig.class)
    static class TestConfig {
        @Bean
        SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            return http
                    .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                    .csrf(csrf -> csrf.disable())
                    .build();
        }
        @Bean
        StubController converterController() {
            return new StubController();
        }
    }


    // -------------------------------------------------- @PathVariable and LocalDate

    @Test
    void testPathLocalDate() throws Exception {
        // given
        String date = "20250305";
        // when
        mockMvc.perform(get("/convert/date/{date}", date))
                // then
                .andExpect(status().isOk())
                .andExpect(content().string(date));
    }

    // ConversionServiceによるparseに失敗した場合、デフォルトでISO書式(LocalDate.parse(String))への
    // パースが試みられる
    @Test
    void testPathLocalDateFallbackToISO() throws Exception {
        // given
        String date = "2025-03-05"; // ISO書式
        // when
        mockMvc.perform(get("/convert/date/{date}", date))
                // then
                .andExpect(status().isOk())
                .andExpect(content().string(date));
    }

    // ConversionServiceによるparseとISOへのフォールバックも失敗した場合はエラー
    @Test
    void testPathLocalDateConvertNG() throws Exception {
        // given
        String date = "2025.03.05";
        // when
        mockMvc.perform(get("/convert/date/{date}", date))
                // then
                .andExpect(status().isBadRequest());
    }

    @Test
    void testPathLocalDateWithPattern() throws Exception {
        // given
        String date = "2025.03.05";
        // when
        mockMvc.perform(get("/convert/date/pattern/{date}", date))
                // then
                .andExpect(status().isOk())
                .andExpect(content().string(date));
    }


    // -------------------------------------------------- @PathVariable and LocalDateTime

    @Test
    void testPathLocalDateTime() throws Exception {
        // given
        String dateTime = "20250305 10:20";
        // when
        mockMvc.perform(get("/convert/datetime/{datetime}", dateTime))
                // then
                .andExpect(status().isOk())
                .andExpect(content().string(dateTime));
    }

    @Test
    void testPathLocalDateTimeFallbackToISO() throws Exception {
        // given
        String dateTime = "2025-03-05T10:20"; // ISO書式
        // when
        mockMvc.perform(get("/convert/datetime/{datetime}", dateTime))
                // then
                .andExpect(status().isOk())
                .andExpect(content().string(dateTime));
    }

    @Test
    void testPathLocalDateTimeConvertNG() throws Exception {
        // given
        String dateTime = "2025.03.05 10:20";
        // when
        mockMvc.perform(get("/convert/datetime/{date}", dateTime))
                // then
                .andExpect(status().isBadRequest());
    }

    @Test
    void testPathLocalDateTimeWithPattern() throws Exception {
        // given
        String dateTime = "2025.03.05 10:20";
        // when
        mockMvc.perform(get("/convert/datetime/pattern/{datetime}", dateTime))
                // then
                .andExpect(status().isOk())
                .andExpect(content().string(dateTime));
    }


    // -------------------------------------------------- @RequestParam and LocalDate

    @Test
    void testParamLocalDate() throws Exception {
        // given
        String date = "20250305";
        // when
        mockMvc.perform(get("/convert/date")
                .param("date", date))
                // then
                .andExpect(status().isOk())
                .andExpect(content().string(date));
    }

    @Test
    void testParamLocalDateFallbackToISO() throws Exception {
        // given
        String date = "2025-03-05"; // ISO書式
        // when
        mockMvc.perform(get("/convert/date")
                .param("date", date))
                // then
                .andExpect(status().isOk())
                .andExpect(content().string(date));
    }

    // ConversionServiceによるparseとISOへのフォールバックも失敗した場合はエラー
    @Test
    void testParamLocalDateConvertNG() throws Exception {
        // given
        String date = "2025.03.05";
        // when
        mockMvc.perform(get("/convert/date")
                .param("date", date))
                // then
                .andExpect(status().isBadRequest());
    }

    @Test
    void testParamLocalDateWithPattern() throws Exception {
        // given
        String date = "2025.03.05";
        // when
        mockMvc.perform(get("/convert/date/pattern")
                .param("date", date))
                // then
                .andExpect(status().isOk())
                .andExpect(content().string(date));
    }


    // -------------------------------------------------- @RequestParam and LocalDateTime

    @Test
    void testParamLocalDateTime() throws Exception {
        // given
        String dateTime = "20250305 10:20";
        // when
        mockMvc.perform(get("/convert/datetime")
                .param("datetime", dateTime))
                // then
                .andExpect(status().isOk())
                .andExpect(content().string(dateTime));
    }

    @Test
    void testParamLocalDateTimeFallbackToISO() throws Exception {
        // given
        String dateTime = "2025-03-05T10:20"; // ISO書式
        // when
        mockMvc.perform(get("/convert/datetime")
                .param("datetime", dateTime))
                // then
                .andExpect(status().isOk())
                .andExpect(content().string(dateTime));
    }

    @Test
    void testParamLocalDateTimeConvertNG() throws Exception {
        // given
        String dateTime = "2025.03.05 10:20";
        // when
        mockMvc.perform(get("/convert/datetime")
                .param("datetime", dateTime))
                // then
                .andExpect(status().isBadRequest());
    }

    @Test
    void testParamLocalDateTimeWithPattern() throws Exception {
        // given
        String dateTime = "2025.03.05 10:20";
        // when
        mockMvc.perform(get("/convert/datetime/pattern")
                .param("datetime", dateTime))
                // then
                .andExpect(status().isOk())
                .andExpect(content().string(dateTime));
    }


    // -------------------------------------------------- JSON (de)serialize for LocalDate / LocalDateTime

    @Test
    void testDateDto() throws Exception {
        // given
        DateStringDto dto = new DateStringDto("20250301", "20250305 10:20");
        String body = mapper.writeValueAsString(dto);

        // when
        mockMvc.perform(post("/convert")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.date").value(dto.date()))
                .andExpect(jsonPath("$.dateTime").value(dto.dateTime()));
    }

    @Test
    void testDateDtoLocalDateConvertNG() throws Exception {
        // given
        DateStringDto dto = new DateStringDto("2025-03-01", "20250305 10:20"); // JSONの場合はISO形式はfallbackされない
        String body = mapper.writeValueAsString(dto);

        // when
        mockMvc.perform(post("/convert")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                // then
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDateDtoLocalDateTimeConvertNG() throws Exception {
        // given
        DateStringDto dto = new DateStringDto("20250301", "2025-03-05 10:20"); // JSONの場合はISO形式はfallbackされない
        String body = mapper.writeValueAsString(dto);

        // when
        mockMvc.perform(post("/convert")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                // then
                .andExpect(status().isBadRequest());
    }

    static record DateStringDto(
            String date,
            String dateTime) {
    }
}