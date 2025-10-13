package io.extact.msa.spring.platform.core.log.access;

import java.util.Map;
import java.util.stream.Collectors;

import ch.qos.logback.access.common.spi.ServerAdapter;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class ServletServerAdapter implements ServerAdapter {

    private final CountableServeletResponseWrapper response;
    private final long requestStartTime;

    @Override
    public long getRequestTimestamp() {
        return requestStartTime;
    }

    @Override
    public long getContentLength() {
        return response.getContentSize();
    }

    @Override
    public int getStatusCode() {
        return response.getStatus();
    }

    @Override
    public Map<String, String> buildResponseHeaderMap() {
        return response.getHeaderNames().stream()
                .collect(Collectors.toMap(key -> key, key -> response.getHeader(key)));
    }
}
