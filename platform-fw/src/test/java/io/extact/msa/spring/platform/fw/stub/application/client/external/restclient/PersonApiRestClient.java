package io.extact.msa.spring.platform.fw.stub.application.client.external.restclient;

import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import io.extact.msa.spring.platform.fw.external.PropagateLoginUserRequestInitializer;
import io.extact.msa.spring.platform.fw.external.PropagateResponseExceptionMapper;
import io.extact.msa.spring.platform.fw.stub.application.common.ClientServerPersonApi;
import jakarta.ws.rs.Path;

@RegisterRestClient(configKey = "web-api")
//@RegisterProvider(RmsTypeParameterFeature.class)
@RegisterProvider(PropagateResponseExceptionMapper.class)
@RegisterClientHeaders(PropagateLoginUserRequestInitializer.class)
@Path("/persons")
public interface PersonApiRestClient extends ClientServerPersonApi {
}
