package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.PaymentDao;
import com.upgrad.FoodOrderingApp.service.entity.PaymentEntity;
import com.upgrad.FoodOrderingApp.service.exception.PaymentMethodNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;

@Service
public class PaymentService {

    @Autowired
    private PaymentDao paymentDao;

    @Transactional
    public PaymentEntity getPaymentByUuid(final String paymentUuid) throws PaymentMethodNotFoundException {
        return paymentDao.getPaymentByUuid(paymentUuid);
    }

    @Transactional
    public List<PaymentEntity> getAllPaymentMethods() {
        return paymentDao.getAllPaymentMethods();
    }
}
