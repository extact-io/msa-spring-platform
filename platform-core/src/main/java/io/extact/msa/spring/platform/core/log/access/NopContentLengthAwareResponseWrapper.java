package io.extact.msa.spring.platform.core.log.access;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

class NopContentLengthAwareResponseWrapper extends HttpServletResponseWrapper implements ContentLengthAwareResponse {

    public NopContentLengthAwareResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    @Override
    public int getContentLength() {
        return -1;
    }
}