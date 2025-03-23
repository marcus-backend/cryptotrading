package com.marcus.model.core;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.marcus.model.AbstractEntity;
import com.marcus.model.auth.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_address")
public class Address extends AbstractEntity<Long> {
    
    @Column(name = "apartment_number")
    private String apartmentNumber;
    
    @Column(name = "floor")
    private String floor;
    
    @Column(name = "building")
    private String building;
    
    @Column(name = "street_number")
    private String streetNumber;
    
    @Column(name = "street")
    private String street;
    
    @Column(name = "city")
    private String city;
    
    @Column(name = "country")
    private String country;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;
    
    @Column(name = "address_type")
    private Integer addressType;
}
