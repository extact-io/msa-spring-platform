package io.extact.msa.spring.platform.fw.stub.auth.client;

import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import io.extact.msa.spring.platform.fw.external.PropagateJwtRequestInitializer;
import io.extact.msa.spring.platform.fw.external.PropagateResponseExceptionMapper;
import io.extact.msa.spring.platform.fw.external.jwt.CustomizableClientRequest;
import io.extact.msa.spring.platform.fw.stub.auth.client_sever1.ClientServer1Api;
import jakarta.ws.rs.Path;

// test-classesは自動でRestClientインタフェースが検出されないので@AddBeanでインタフェースを登録すること
@RegisterRestClient(configKey = "web-api")
//@RegisterProvider(RmsTypeParameterFeature.class)
@RegisterProvider(PropagateResponseExceptionMapper.class)
@RegisterProvider(CustomizableClientRequest.class)
@RegisterClientHeaders(PropagateJwtRequestInitializer.class)
@Path("/server1")
public interface Server1ApiRestClient extends ClientServer1Api {
}
