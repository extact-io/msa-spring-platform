package io.extact.msa.spring.platform.test.stub.dto;

import io.extact.msa.spring.platform.fw.domain.Transformable;
import io.extact.msa.spring.platform.fw.domain.vo.UserType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@Getter @Setter @ToString
public class AddUserAccountStubDto implements Transformable {
    private String loginId;
    private String password;
    private String userName;
    private String phoneNumber;
    private String contact;
    private UserType userType;
}
