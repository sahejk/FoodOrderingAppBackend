package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CouponDao;
import com.upgrad.FoodOrderingApp.service.entity.CouponEntity;
import com.upgrad.FoodOrderingApp.service.exception.CouponNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class CouponService {

    //Declaring a variable called couponDao
    @Autowired
    private CouponDao couponDao;

    @Transactional
    public CouponEntity getCouponById(final Long couponId) {
        return couponDao.getCouponById(couponId);
    }

    //throws an exception CuponNotFoundException
    @Transactional
    public CouponEntity getCouponByUuid(final String couponUuid) throws CouponNotFoundException {
        return couponDao.getCouponByUuid(couponUuid);
    }
}
