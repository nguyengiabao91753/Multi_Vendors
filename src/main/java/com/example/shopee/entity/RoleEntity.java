package com.example.shopee.entity;

import com.example.shopee.enums.RoleEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Set;

@Data
@ToString
@Getter
@Entity
@Table(name = "Roles")
public class RoleEntity extends AbstractEntity {

    @Enumerated(EnumType.STRING)
    @Column(length = 255, unique = true, columnDefinition = "nvarchar(255)")
    private RoleEnum name;

    @ManyToMany(fetch = FetchType.EAGER,mappedBy = "roleEntities")
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    private Set<UserEntity> userEntities;

}
