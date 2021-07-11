package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class ItemDao {

    @PersistenceContext
    private EntityManager entityManager;

    public ItemEntity getItemByUuid(final String itemUuid) {
        try {
            return entityManager.createNamedQuery("itemByUuid", ItemEntity.class).setParameter("uuid", itemUuid)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }
}
