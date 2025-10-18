package io.extact.msa.spring.platform.core.log.access;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Optional;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

class ContentLengthAwareResponseWrapper extends HttpServletResponseWrapper implements ContentLengthAwareResponse {

    private CountableResponseOutputStream outputStream;
    private PrintWriter writer;

    public ContentLengthAwareResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    @Override
    public int getContentLength() {
        return Optional.ofNullable(outputStream)
                .map(CountableResponseOutputStream::getCount)
                .orElse(0);
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (outputStream == null) {
            outputStream = new CountableResponseOutputStream(this.getResponse().getOutputStream());
        }
        return outputStream;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (writer == null) {
            OutputStreamWriter osw = new OutputStreamWriter(
                    this.getOutputStream(),
                    this.getResponse().getCharacterEncoding());
            writer = new PrintWriter(osw, true);
        }
        return writer;
    }

    @Override
    public void flushBuffer() {
        if (this.writer != null) {
            this.writer.flush();
        }
    }

    @RequiredArgsConstructor
    static class CountableResponseOutputStream extends ServletOutputStream {

        @Getter
        private int count = 0;
        private final ServletOutputStream os;

        @Override
        public void write(int b) throws IOException {
            count++;
            os.write(b);
        }

        @Override
        public void write(byte[] b) throws IOException {
            count += b.length;
            os.write(b);
        }

        @Override
        public void write(byte[] b, int offset, int length) throws IOException {
            count += length;
            os.write(b, offset, length);
        }

        @Override
        public void close() throws IOException {
            os.close();
        }

        @Override
        public void flush() throws IOException {
            os.flush();
        }

        @Override
        public boolean isReady() {
            return os.isReady();
        }

        @Override
        public void setWriteListener(WriteListener listener) {
            os.setWriteListener(listener);
        }
    }
}