package io.extact.msa.spring.platform.core.log.access;

import jakarta.servlet.http.HttpServletResponse;

public interface ContentLengthAwareResponse extends HttpServletResponse {
    int getContentLength();
}
