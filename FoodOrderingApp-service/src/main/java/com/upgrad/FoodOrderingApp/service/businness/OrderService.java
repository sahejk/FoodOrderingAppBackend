package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CustomerAddressDao;
import com.upgrad.FoodOrderingApp.service.dao.OrderDao;
import com.upgrad.FoodOrderingApp.service.dao.OrderItemDao;
import com.upgrad.FoodOrderingApp.service.entity.*;
import com.upgrad.FoodOrderingApp.service.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private CustomerBusinessService customerBusinessService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private CouponService couponService;

    @Autowired
    private  ItemService itemService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private OrderItemDao orderItemDao;

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private CustomerAddressDao customerAddressDao;

    @Autowired
    private RestaurantBusinessService restaurantBusinessService;


    @Transactional
    public CouponEntity getCouponByName(String couponName) throws AuthorizationFailedException, CouponNotFoundException {
        // Validates the access token retrieved from database
        return orderDao.getCouponByName(couponName);
    }

    @Transactional
    public List<OrdersEntity> getCustomerOrders(final CustomerEntity customerEntity) {
        return orderDao.getCustomerOrders(customerEntity);
    }

    @Transactional
    public OrdersEntity saveOrder(AddressEntity addressEntity, CouponEntity couponEntity, PaymentEntity paymentEntity, RestaurantEntity restaurantEntity, ArrayList<OrderItemEntity> orderItemEntities, OrdersEntity ordersEntity, CustomerEntity customerEntity)
            throws AuthorizationFailedException, CouponNotFoundException, AddressNotFoundException,
            PaymentMethodNotFoundException, RestaurantNotFoundException, ItemNotFoundException {

        // Gets the Customer address details from customerAddressDao
        CustomerAddressEntity customerAddressEntity = customerAddressDao.getCustAddressByCustIdAddressId(customerEntity, addressEntity);

        // Throws CouponNotFoundException if coupon not found
        if (couponEntity == null) {
            throw new CouponNotFoundException("CPF-002", "No coupon by this id");
            // Throws AddressNotFoundException if address not found
        } else if (addressEntity == null) {
            throw new AddressNotFoundException("ANF-003", "No address by this id");
            // Throws PaymentMethodNotFoundException id payment method not found
        } else if (paymentEntity ==  null) {
            throw new PaymentMethodNotFoundException("PNF-002", "No payment method found by this id");
            // Throws RestaurantNotFoundException if restaurant not found
        } else if (restaurantEntity == null) {
            throw new RestaurantNotFoundException("RNF-001", "No restaurant by this id");
            // Throws AuthorizationFailedException if customer provides some other's address
        } else if (customerAddressEntity == null) {
            throw new AuthorizationFailedException("ATHR-004", "You are not authorized to view/update/delete any one else's address");
        }

        final ZonedDateTime now = ZonedDateTime.now();

        // Loads the ordersEntity with all the obtained details

        // Saves the order by calling saveOrder
        ordersEntity.setCustomer(customerEntity);
        ordersEntity.setDate(now);
        OrdersEntity savedOrderEntity = orderDao.saveOrder(ordersEntity);

        for(OrderItemEntity orderItemEntity: orderItemEntities) {
            // Saves the order item by calling createOrderItemEntity of orderItemDao
            orderItemEntity.setOrder(savedOrderEntity);
            orderItemDao.createOrderItemEntity(orderItemEntity);
        }
        // Returns the savedOrderEntity from the orderDao
        return orderDao.saveOrder(savedOrderEntity);
    }
}
