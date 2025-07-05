package com.example.shopee.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Set;

@Data
@Entity
@Table(name = "Cart")
public class CartEntity extends AbstractEntity{

    @Basic
    @Column(name = "total_cost", nullable = true)
    private BigDecimal totalCost;

    @OneToOne(mappedBy = "cartEntity")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonIgnore
    private UserEntity userEntity;

    @OneToMany(mappedBy = "cartEntity", cascade = CascadeType.ALL,fetch = FetchType.LAZY)
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	@JsonManagedReference
    private Set<CartDetailEntity> cartDetailEntities;

	public BigDecimal getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(BigDecimal totalCost) {
		this.totalCost = totalCost;
	}

	public UserEntity getUserEntity() {
		return userEntity;
	}

	public void setUserEntity(UserEntity userEntity) {
		this.userEntity = userEntity;
	}

	public Set<CartDetailEntity> getCartDetailEntities() {
		return cartDetailEntities;
	}

	public void setCartDetailEntities(Set<CartDetailEntity> cartDetailEntities) {
		this.cartDetailEntities = cartDetailEntities;
	}
}
