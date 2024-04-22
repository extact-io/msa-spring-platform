package io.extact.msa.spring.platform.test.stub.dto;

import io.extact.msa.spring.platform.fw.domain.Transformable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@Getter @Setter @ToString
public class AddRentalItemStubDto implements Transformable {
    private String serialNo;
    private String itemName;
}
