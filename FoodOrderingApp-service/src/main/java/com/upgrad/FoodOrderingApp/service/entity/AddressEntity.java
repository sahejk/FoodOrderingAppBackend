package com.upgrad.FoodOrderingApp.service.entity;


import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table (name = "address",uniqueConstraints = {@UniqueConstraint(columnNames = {"uuid"})})
@NamedQueries(
        {
                @NamedQuery(name = "addressByUuid", query = "select a from AddressEntity a where a.uuid =:uuid"),
                @NamedQuery(name = "allAddresses", query = "select a from AddressEntity a "),
                @NamedQuery(name = "addressById", query = "select a from AddressEntity a where a.id=:id")
        }
)public class AddressEntity {


    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "uuid")
    @Size(max = 200)
    @NotNull
    private String uuid;

    @Column(name = "flat_buil_number")
    @Size(max = 255)
    private String flatBuilNumber;

    @Column(name = "locality")
    @Size(max = 255)
    private String locality;

    @Column(name = "city")
    @Size(max = 30)
    private String city;

    @Column(name = "pincode")
    @Size(max = 30)
    private String pincode;

    @Column(name = "active")
    private Integer active;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "state_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private StateEntity stateName;

    public AddressEntity(String addressId, String s, String someLocality, String someCity, String s1, StateEntity stateEntity) {
        this.uuid = addressId;
        this.locality = someLocality;
        this.city = someCity;
        this.stateName = stateEntity;
        this.pincode = s1;
        this.flatBuilNumber = s;
    }

    public AddressEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getFlatBuilNumber() {
        return flatBuilNumber;
    }

    public void setFlatBuilNumber(String flatBuilNumber) {
        this.flatBuilNumber = flatBuilNumber;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }

    public StateEntity getStateName() {
        return stateName;
    }

    public void setStateName(StateEntity stateName) {
        this.stateName = stateName;
    }
}