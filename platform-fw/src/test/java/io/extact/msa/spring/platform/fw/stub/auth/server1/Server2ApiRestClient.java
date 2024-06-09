package io.extact.msa.spring.platform.fw.stub.auth.server1;

import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import io.extact.msa.spring.platform.fw.external.PropagateLoginUserRequestInitializer;
import io.extact.msa.spring.platform.fw.external.PropagateResponseExceptionMapper;
import io.extact.msa.spring.platform.fw.stub.auth.server1_server2.ClientServer2Api;
import jakarta.ws.rs.Path;

//test-classesは自動でRestClientインタフェースが検出されないので@AddBeanでインタフェースを登録すること
@RegisterRestClient(configKey = "web-api")
//@RegisterProvider(RmsTypeParameterFeature.class)
@RegisterProvider(PropagateResponseExceptionMapper.class)
@RegisterClientHeaders(PropagateLoginUserRequestInitializer.class)
@Path("/server2")
public interface Server2ApiRestClient extends ClientServer2Api {
}
