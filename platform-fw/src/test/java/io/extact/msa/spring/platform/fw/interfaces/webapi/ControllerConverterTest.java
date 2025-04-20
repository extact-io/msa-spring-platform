package io.extact.msa.spring.platform.fw.interfaces.webapi;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest
class ControllerConverterTest {

    @Autowired
    private MockMvcTester mockMvc;
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
        MvcTestResult result = mockMvc
                .get()
                .uri("/convert/date/{date}", date)
                .exchange();
        // then
        assertThat(result)
                .hasStatusOk()
                .hasBodyTextEqualTo(date);
    }

    // ConversionServiceによるparseに失敗した場合、デフォルトでISO書式(LocalDate.parse(String))への
    // パースが試みられる
    @Test
    void testPathLocalDateFallbackToISO() throws Exception {
        // given
        String date = "2025-03-05"; // ISO書式
        // when
        MvcTestResult result = mockMvc
                .get()
                .uri("/convert/date/{date}", date)
                .exchange();
        // then
        assertThat(result)
                .hasStatusOk()
                .hasBodyTextEqualTo(date);
    }

    // ConversionServiceによるparseとISOへのフォールバックも失敗した場合はエラー
    @Test
    void testPathLocalDateConvertNG() throws Exception {
        // given
        String date = "2025.03.05";
        // when
        MvcTestResult result = mockMvc
                .get()
                .uri("/convert/date/{date}", date)
                .exchange();
        // then
        assertThat(result)
                .hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    void testPathLocalDateWithPattern() throws Exception {
        // given
        String date = "2025.03.05";
        // when
        MvcTestResult result = mockMvc
                .get()
                .uri("/convert/date/pattern/{date}", date)
                .exchange();
        // then
        assertThat(result)
                .hasStatusOk()
                .hasBodyTextEqualTo(date);
    }

    // -------------------------------------------------- @PathVariable and LocalDateTime

    @Test
    void testPathLocalDateTime() throws Exception {
        // given
        String dateTime = "20250305 10:20";
        // when
        MvcTestResult result = mockMvc
                .get()
                .uri("/convert/datetime/{datetime}", dateTime)
                .exchange();
        // then
        assertThat(result)
                .hasStatusOk()
                .hasBodyTextEqualTo(dateTime);
    }

    @Test
    void testPathLocalDateTimeFallbackToISO() throws Exception {
        // given
        String dateTime = "2025-03-05T10:20"; // ISO書式
        // when
        MvcTestResult result = mockMvc
                .get()
                .uri("/convert/datetime/{datetime}", dateTime)
                .exchange();
        // then
        assertThat(result)
                .hasStatusOk()
                .hasBodyTextEqualTo(dateTime);
    }

    @Test
    void testPathLocalDateTimeConvertNG() throws Exception {
        // given
        String dateTime = "2025.03.05 10:20";
        // when
        MvcTestResult result = mockMvc
                .get()
                .uri("/convert/datetime/{datetime}", dateTime)
                .exchange();
        // then
        assertThat(result)
                .hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    void testPathLocalDateTimeWithPattern() throws Exception {
        // given
        String dateTime = "2025.03.05 10:20";
        // when
        MvcTestResult result = mockMvc
                .get()
                .uri("/convert/datetime/pattern/{datetime}", dateTime)
                .exchange();
        // then
        assertThat(result)
                .hasStatusOk()
                .hasBodyTextEqualTo(dateTime);
    }


    // -------------------------------------------------- @RequestParam and LocalDate

    @Test
    void testParamLocalDate() throws Exception {
        // given
        String date = "20250305";
        // when
        MvcTestResult result = mockMvc
                .get()
                .uri("/convert/date")
                .param("date", date)
                .exchange();
        // then
        assertThat(result)
                .hasStatusOk()
                .hasBodyTextEqualTo(date);
    }

    @Test
    void testParamLocalDateFallbackToISO() throws Exception {
        // given
        String date = "2025-03-05"; // ISO書式
        // when
        MvcTestResult result = mockMvc
                .get()
                .uri("/convert/date")
                .param("date", date)
                .exchange();
        // then
        assertThat(result)
                .hasStatusOk()
                .hasBodyTextEqualTo(date);
    }

    // ConversionServiceによるparseとISOへのフォールバックも失敗した場合はエラー
    @Test
    void testParamLocalDateConvertNG() throws Exception {
        // given
        String date = "2025.03.05";
        // when
        MvcTestResult result = mockMvc
                .get()
                .uri("/convert/date")
                .param("date", date)
                .exchange();
        // then
        assertThat(result)
                .hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    void testParamLocalDateWithPattern() throws Exception {
        // given
        String date = "2025.03.05";
        // when
        MvcTestResult result = mockMvc
                .get()
                .uri("/convert/date/pattern")
                .param("date", date)
                .exchange();
        // then
        assertThat(result)
                .hasStatusOk()
                .hasBodyTextEqualTo(date);
    }


    // -------------------------------------------------- @RequestParam and LocalDateTime

    @Test
    void testParamLocalDateTime() throws Exception {
        // given
        String dateTime = "20250305 10:20";
        // when
        MvcTestResult result = mockMvc
                .get()
                .uri("/convert/datetime")
                .param("datetime", dateTime)
                .exchange();
        // then
        assertThat(result)
                .hasStatusOk()
                .hasBodyTextEqualTo(dateTime);
    }

    @Test
    void testParamLocalDateTimeFallbackToISO() throws Exception {
        // given
        String dateTime = "2025-03-05T10:20"; // ISO書式
        // when
        MvcTestResult result = mockMvc
                .get()
                .uri("/convert/datetime")
                .param("datetime", dateTime)
                .exchange();
        // then
        assertThat(result)
                .hasStatusOk()
                .hasBodyTextEqualTo(dateTime);
    }

    @Test
    void testParamLocalDateTimeConvertNG() throws Exception {
        // given
        String dateTime = "2025.03.05 10:20";
        // when
        MvcTestResult result = mockMvc
                .get()
                .uri("/convert/datetime")
                .param("datetime", dateTime)
                .exchange();
        // then
        assertThat(result)
                .hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    void testParamLocalDateTimeWithPattern() throws Exception {
        // given
        String dateTime = "2025.03.05 10:20";
        // when
        MvcTestResult result = mockMvc
                .get()
                .uri("/convert/datetime/pattern")
                .param("datetime", dateTime)
                .exchange();
        // then
        assertThat(result)
                .hasStatusOk()
                .hasBodyTextEqualTo(dateTime);
    }


    // -------------------------------------------------- JSON (de)serialize for LocalDate / LocalDateTime

    @Test
    void testDateDto() throws Exception {
        // given
        DateStringDto dto = new DateStringDto("20250301", "20250305 10:20");
        String body = mapper.writeValueAsString(dto);

        // when
        MvcTestResult result = mockMvc
                .post()
                .uri("/convert")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .exchange();

        // then
        assertThat(result)
                .hasStatusOk()
                .bodyJson()

                /* ----
                 * convertToでDTOにバインドもできるが、convertToが行うConverterとかも絡んでくる
                 * Convereterとか例外ハンドリングとはTestRestTemplateやHttpInterfaceを使った統合テストでやるべき
                 * それに対するものとしてControllerの単体なので、JSON文字列を検証するのが妥当と思うため
                 * convertToは使わずJSONパスで生でデータを検証するようにしている
                 */
                .hasPathSatisfying("$.date", p -> p.assertThat().isEqualTo(dto.date()))
                .hasPathSatisfying("$.dateTime", p -> p.assertThat().isEqualTo(dto.dateTime()));
    }

    @Test
    void testDateDtoLocalDateConvertNG() throws Exception {
        // given
        DateStringDto dto = new DateStringDto("2025-03-01", "20250305 10:20"); // JSONの場合はISO形式はfallbackされない
        String body = mapper.writeValueAsString(dto);

        // when
        MvcTestResult result = mockMvc
                .post()
                .uri("/convert")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .exchange();

        // then
        assertThat(result)
                .hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    void testDateDtoLocalDateTimeConvertNG() throws Exception {
        // given
        DateStringDto dto = new DateStringDto("20250301", "2025-03-05 10:20"); // JSONの場合はISO形式はfallbackされない
        String body = mapper.writeValueAsString(dto);

        // when
        MvcTestResult result = mockMvc
                .post()
                .uri("/convert")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .exchange();

        // then
        assertThat(result)
                .hasStatus(HttpStatus.BAD_REQUEST);
    }

    static record DateStringDto(
            String date,
            String dateTime) {
    }
}