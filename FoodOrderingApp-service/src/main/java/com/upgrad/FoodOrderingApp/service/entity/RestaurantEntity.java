package com.upgrad.FoodOrderingApp.service.entity;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "restaurant",uniqueConstraints = {@UniqueConstraint(columnNames = {"uuid"})})
@NamedQueries(
        {
                @NamedQuery(name = "allRestaurants", query = "select r from RestaurantEntity r order by r.customerRating desc"),
                @NamedQuery(name = "findByName", query = "select r from RestaurantEntity  r where lower(r.restaurantName) like :restaurantName order by r.restaurantName"),
                @NamedQuery(name = "findRestaurantByUUId",query = "select r from RestaurantEntity r where lower(r.uuid) = :restaurantUUID")
        }
)
public class RestaurantEntity {


    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "uuid")
    @Size(max = 200)
    @NotNull
    private String uuid;

    @Column(name = "restaurant_name")
    @Size(max = 50)
    @NotNull
    private String restaurantName;

    @Column(name = "photo_url")
    @Size(max = 255)
    private String photoURL;

    @Column(name = "customer_rating")
    @NotNull
    private BigDecimal customerRating;

    @Column(name = "average_price_for_two")
    @NotNull
    private Integer averagePriceForTwo;

    @Column(name = "number_of_customer_rated")
    @NotNull
    private Integer numberOfCustomerRated;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "address_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private AddressEntity address;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "restaurant_category",
            joinColumns = @JoinColumn(name = "restaurant_id", referencedColumnName="id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "category_id", referencedColumnName="id", nullable = false)
    )
    private Set<CategoryEntity> categoryEntities = new HashSet<>();


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    public BigDecimal getCustomerRating() {
        return customerRating;
    }

    public void setCustomerRating(BigDecimal customerRating) {
        this.customerRating = customerRating;
    }

    public Integer getAveragePriceForTwo() {
        return averagePriceForTwo;
    }

    public void setAveragePriceForTwo(Integer averagePriceForTwo) {
        this.averagePriceForTwo = averagePriceForTwo;
    }

    public Integer getNumberOfCustomerRated() {
        return numberOfCustomerRated;
    }

    public void setNumberOfCustomerRated(Integer numberOfCustomerRated) {
        this.numberOfCustomerRated = numberOfCustomerRated;
    }

    public AddressEntity getAddress() {
        return address;
    }

    public void setAddress(AddressEntity address) {
        this.address = address;
    }

    public Set<CategoryEntity> getCategoryEntities() {
        return categoryEntities;
    }

    public void setCategoryEntities(Set<CategoryEntity> categoryEntities) {
        this.categoryEntities = categoryEntities;
    }

}