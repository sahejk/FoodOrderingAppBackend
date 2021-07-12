package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.RestaurantDao;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantCategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.InvalidRatingException;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

@Service
public class RestaurantBusinessService {

    @Autowired
    private RestaurantDao restaurantDao;

    @Autowired
    private CustomerBusinessService customerBusinessService;

    // A Method which takes the restaurantUUID as parameter for  getRestaurantByUUId endpoint
    public RestaurantEntity getRestaurantByUUId(String restaurantUUID) throws RestaurantNotFoundException {
        return restaurantDao.getRestaurantByUUId(restaurantUUID);
    }

    // A Method is for getAllRestaurants endpoint
    public List<RestaurantEntity> getAllRestaurants() {
        return restaurantDao.getAllRestaurants();
    }

    // A Method which takes the restaurantName as parameter for  getRestaurantsByName endpoint
    public List<RestaurantEntity> getRestaurantsByName(String restaurantName) throws RestaurantNotFoundException {
        return restaurantDao.getRestaurantsByName(restaurantName);
    }

    @Transactional
    public RestaurantEntity updateCustomerRating (final Double customerRating, final String restaurant_id, final CustomerEntity customerEntity)
            throws AuthorizationFailedException, RestaurantNotFoundException, InvalidRatingException {

        // Throw exception if path variable(restaurant_id) is empty
        if(restaurant_id == null || restaurant_id.isEmpty() || restaurant_id.equalsIgnoreCase("\"\"")){
            throw new RestaurantNotFoundException("RNF-002", "Restaurant id field should not be empty");
        }

        RestaurantEntity restaurantEntity = getRestaurantEntity(restaurant_id);

        // Throw exception if path variable(restaurant_id) is empty
        if(customerRating == null || customerRating.isNaN() || customerRating < 1 || customerRating > 5 ){
            throw new InvalidRatingException("IRE-001", "Restaurant should be in the range of 1 to 5");
        }

        // Now calculate new customer rating  and set the updated rating and attach it to the restaurantEntity
        BigDecimal oldRatingCalculation = (restaurantEntity.getCustomerRating().multiply(new BigDecimal(restaurantEntity.getNumberOfCustomerRated())));
        BigDecimal calculatedRating = (oldRatingCalculation.add(new BigDecimal(customerRating))).divide(new BigDecimal(restaurantEntity.getNumberOfCustomerRated() + 1));
        restaurantEntity.setCustomerRating(calculatedRating);
        restaurantEntity.setNumberOfCustomerRated(restaurantEntity.getNumberOfCustomerRated() + 1);

        //called restaurantDao to merge the content and update in the database
        restaurantDao.updateRestaurant(restaurantEntity);
        return restaurantEntity;
    }

    public RestaurantEntity getRestaurantEntity(String restaurant_id) throws RestaurantNotFoundException{
        //get the restaurant Details using the restaurantUuid
        RestaurantEntity restaurantEntity =  restaurantDao.getRestaurantByUUId(restaurant_id);
        if (restaurantEntity == null) {
            throw new RestaurantNotFoundException("RNF-001", "No restaurant by this id");
        }
        return restaurantEntity;
    }

    // A Method which takes the categoryUUID as parameter for  getRestaurantByCategoryId endpoint
    public List<RestaurantCategoryEntity> getRestaurantByCategoryId(final Long categoryID) throws CategoryNotFoundException {
        return restaurantDao.getRestaurantByCategoryId(categoryID);
    }
}
