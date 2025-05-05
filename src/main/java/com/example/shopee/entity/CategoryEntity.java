package com.example.shopee.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Set;

@Data
@Entity
@Table(name = "Categories")
public class CategoryEntity extends AbstractEntity {
    @Basic
    @Column(name = "CategoryName", nullable = true, length = 255, columnDefinition = "nvarchar(255)")
    private String categoryName;

    @OneToMany(mappedBy = "categoryEntity", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<ProductEntity> productEntities;

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public Set<ProductEntity> getProductEntities() {
		return productEntities;
	}

	public void setProductEntities(Set<ProductEntity> productEntities) {
		this.productEntities = productEntities;
	}
    
    
}
