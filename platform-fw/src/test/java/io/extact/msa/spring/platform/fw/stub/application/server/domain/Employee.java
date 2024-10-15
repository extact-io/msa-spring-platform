package io.extact.msa.spring.platform.fw.stub.application.server.domain;

import static jakarta.persistence.AccessType.*;

import jakarta.persistence.Access;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import io.extact.msa.spring.platform.fw.domain.Identifiable;
import io.extact.msa.spring.platform.fw.domain.Transformable;
import io.extact.msa.spring.platform.fw.domain.constraint.RmsId;
import io.extact.msa.spring.platform.fw.domain.constraint.ValidationGroups.Delete;
import io.extact.msa.spring.platform.fw.domain.constraint.ValidationGroups.Update;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Access(FIELD)
@Entity
@NoArgsConstructor
@AllArgsConstructor(staticName = "valueOf")
@Getter @Setter
@ToString
public class Employee implements Transformable, Identifiable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @RmsId(groups = { Update.class, Delete.class })
    private Integer id;
    private String name;
    private String deptName;

    public static Employee ofTransient(String name, String deptName) {
        return Employee.valueOf(null, name, deptName);
    }
}
