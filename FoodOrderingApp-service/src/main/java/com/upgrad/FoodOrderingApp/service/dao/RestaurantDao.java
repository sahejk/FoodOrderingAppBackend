package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class RestaurantDao {
    @PersistenceContext
    private EntityManager entityManager;

    public RestaurantEntity getRestaurantByUUId(String restaurantUUID) {
        try {
            return entityManager.createNamedQuery("findRestaurantByUUId", RestaurantEntity.class).setParameter("restaurantUUID",restaurantUUID.toLowerCase()).getSingleResult();
        } catch(NoResultException nre) {
            return null;
        }
    }
}
