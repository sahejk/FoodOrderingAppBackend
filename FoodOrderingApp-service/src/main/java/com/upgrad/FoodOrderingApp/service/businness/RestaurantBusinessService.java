package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.RestaurantDao;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RestaurantBusinessService {

    @Autowired
    private RestaurantDao restaurantDao;

    // A Method which takes the restaurantUUID as parameter for  getRestaurantByUUId endpoint
    public RestaurantEntity getRestaurantByUUId(String restaurantUUID) {
        return restaurantDao.getRestaurantByUUId(restaurantUUID);
    }
}
