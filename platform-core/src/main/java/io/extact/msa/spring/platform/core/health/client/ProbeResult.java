package io.extact.msa.spring.platform.core.health.client;

import org.springframework.boot.actuate.health.Status;

public record ProbeResult(String probeUrl, Status status) {
}
