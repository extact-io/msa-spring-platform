package io.extact.msa.spring.platform.fw.it;

import io.helidon.microprofile.tests.junit5.AddConfig;

@AddConfig(key = "rms.persistence.apiType", value = "jpa")
class JpaApplicationIntegrationTest extends AbstractApplicationIntegrationTest {
}
