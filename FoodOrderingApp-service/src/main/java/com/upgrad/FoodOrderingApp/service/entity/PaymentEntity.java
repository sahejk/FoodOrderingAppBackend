package com.upgrad.FoodOrderingApp.service.entity;


import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "payment",uniqueConstraints = {@UniqueConstraint(columnNames = {"uuid"})})
@NamedQueries(
        {
                @NamedQuery(name = "allPaymentMethods", query = "select p from PaymentEntity p "),
                @NamedQuery(name = "paymentById", query = "select p from PaymentEntity p where p.id=:id"),
                @NamedQuery(name = "paymentByUuid", query = "select p from PaymentEntity p where p.uuid=:uuid"),
        }
)
public class PaymentEntity {


    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "uuid")
    @Size(max = 200)
    @NotNull
    private String uuid;

    @Column(name = "payment_name")
    @Size(max = 255)
    private String paymentName;

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

    public String getPaymentName() {
        return paymentName;
    }

    public void setPaymentName(String paymentName) {
        this.paymentName = paymentName;
    }
}