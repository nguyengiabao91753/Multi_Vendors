package com.example.shopee.entity;

import com.example.shopee.utils.DateUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "Users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "username"),
                @UniqueConstraint(columnNames = "email")
        },
        catalog = "")
@Data
public class UserEntity extends AbstractEntity {
    @NotBlank
    @Size(max = 50)
    private String email;

    @NotBlank
    @Size(max = 120)
    private String password;

    @Basic
    @Column(name = "avatar")
    private String avatar;

    @Basic
    @Column(name = "full_name", nullable = true, length = 255, columnDefinition = "nvarchar(255)")
    private String fullName;


    @Basic
    @Column(name = "phone", nullable = true, length = 20)
    private String phone;

    @Basic
    @Column(name = "address", nullable = true, length = 255, columnDefinition = "nvarchar(255)")
    private String address;

    @DateTimeFormat(pattern = DateUtils.DATE_FORMAT)
    @Column(name = "dob", nullable = true)
    private LocalDate dob;

    @OneToOne(cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JoinColumn(name = "cart_id", referencedColumnName = "id")
    private CartEntity cartEntity;

    @OneToMany(mappedBy = "userEntity", cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<FeedbackEntity> feedbackEntities;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	@JsonManagedReference
	private Set<ProductEntity> productEntities;

	@OneToMany(mappedBy = "userEntity", cascade = CascadeType.ALL,fetch = FetchType.LAZY)
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	private Set<OrderEntity> orderEntities;

	@OneToMany(mappedBy = "userEntity", cascade = CascadeType.ALL,fetch = FetchType.LAZY)
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	private Set<VoucherEntity> vouchers;

	@OneToMany(mappedBy = "userEntity", cascade = CascadeType.ALL,fetch = FetchType.LAZY)
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	private Set<ReceiptEntity> receiptEntities;

    @ManyToMany(fetch = FetchType.EAGER)
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name="user_id"),
            inverseJoinColumns = @JoinColumn(name="role_id"))
    private Set<RoleEntity> roleEntities;


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public String getAvatar() {
		return avatar;
	}


	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}


	public String getFullName() {
		return fullName;
	}


	public void setFullName(String fullName) {
		this.fullName = fullName;
	}


	public String getPhone() {
		return phone;
	}


	public void setPhone(String phone) {
		this.phone = phone;
	}


	public String getAddress() {
		return address;
	}


	public void setAddress(String address) {
		this.address = address;
	}


	public LocalDate getDob() {
		return dob;
	}


	public void setDob(LocalDate dob) {
		this.dob = dob;
	}


	public CartEntity getCartEntity() {
		return cartEntity;
	}


	public void setCartEntity(CartEntity cartEntity) {
		this.cartEntity = cartEntity;
	}


	public Set<FeedbackEntity> getFeedbackEntities() {
		return feedbackEntities;
	}


	public void setFeedbackEntities(Set<FeedbackEntity> feedbackEntities) {
		this.feedbackEntities = feedbackEntities;
	}


	public Set<RoleEntity> getRoleEntities() {
		return roleEntities;
	}


	public void setRoleEntities(Set<RoleEntity> roleEntities) {
		this.roleEntities = roleEntities;
	}

}
