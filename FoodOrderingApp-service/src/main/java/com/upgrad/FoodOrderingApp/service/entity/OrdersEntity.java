package com.upgrad.FoodOrderingApp.service.entity;


import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Date;

@Entity
@Table(name = "orders",uniqueConstraints = {@UniqueConstraint(columnNames = {"uuid"})})
@NamedQueries(
        {
                @NamedQuery(name = "ordersByUuid",query="select o from OrdersEntity o where o.uuid=:uuid"),
                @NamedQuery(name = "ordersById", query = "select o from OrdersEntity o where o.id=:id"),
                @NamedQuery(name = "ordersByCustomer", query = "select o from OrdersEntity o where o.customer=:customer order by o.date desc"),
                @NamedQuery(name = "ordersByRestaurant", query = "select o from OrdersEntity o where o.restaurant=:restaurant order by o.date desc"),
        }
)
public class OrdersEntity {


    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "uuid")
    @Size(max = 200)
    @NotNull
    private String uuid;


    @Column(name = "bill")
    @NotNull
    private BigDecimal bill;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "coupon_id")
    private CouponEntity coupon;

    @Column(name = "discount")
    private BigDecimal discount;

    @Column(name = "date")
    @NotNull
    private ZonedDateTime date;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "payment_id")
    private PaymentEntity payment;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id")
    @NotNull
    private CustomerEntity customer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "address_id")
    @NotNull
    private AddressEntity address;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "restaurant_id")
    @NotNull
    private RestaurantEntity restaurant;

    public OrdersEntity(String orderId, double v, CouponEntity couponEntity, double v1, Date orderDate, PaymentEntity paymentEntity, CustomerEntity customerEntity, AddressEntity addressEntity, RestaurantEntity restaurantEntity) {

    }

    public OrdersEntity() {

    }


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

    public BigDecimal getBill() {
        return bill;
    }

    public void setBill(BigDecimal bill) {
        this.bill = bill;
    }

    public CouponEntity getCoupon() {
        return coupon;
    }

    public void setCoupon(CouponEntity coupon) {
        this.coupon = coupon;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public PaymentEntity getPayment() {
        return payment;
    }

    public void setPayment(PaymentEntity payment) {
        this.payment = payment;
    }

    public CustomerEntity getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerEntity customer) {
        this.customer = customer;
    }

    public AddressEntity getAddress() {
        return address;
    }

    public void setAddress(AddressEntity address) {
        this.address = address;
    }

    public RestaurantEntity getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(RestaurantEntity restaurant) {
        this.restaurant = restaurant;
    }
}