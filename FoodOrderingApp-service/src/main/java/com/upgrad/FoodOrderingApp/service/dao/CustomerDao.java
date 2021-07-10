package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class CustomerDao {


    @PersistenceContext
    private EntityManager entityManager;


    public CustomerEntity getCustomerByContactNumber (final String contact_number){
        try{
            CustomerEntity customer = entityManager.createNamedQuery("customerByContactNumber",CustomerEntity.class).setParameter("contact_number",contact_number).getSingleResult();
            return customer;
        }catch (NoResultException nre){
            return null;
        }
    }


    public CustomerEntity createCustomer(CustomerEntity customerEntity){
        entityManager.persist(customerEntity);
        return customerEntity;
    }

    // Creates auth token by persisting the record in the database
    public CustomerAuthEntity createAuthToken(final CustomerAuthEntity customerAuthTokenEntity) {
        entityManager.persist(customerAuthTokenEntity);
        return customerAuthTokenEntity;
    }

    // Updates the customer details to the database
    public void updateCustomer(CustomerEntity updatedCustomerEntity) {
        entityManager.merge(updatedCustomerEntity);
    }

    public CustomerAuthEntity getCustomerAuthToken(String accessToken) {
        try {
            return entityManager.createNamedQuery("customerAuthByAccessToken", CustomerAuthEntity.class)
                    .setParameter("accessToken", accessToken).getSingleResult();
        } catch(NoResultException nre) {
            return null;
        }
    }

    public void updateCustomerAuth(CustomerAuthEntity customerAuthTokenEntity) {
        entityManager.merge(customerAuthTokenEntity);
    }

    public CustomerEntity getCustomerByUuid(String uuid) {
        try {
            return entityManager.createNamedQuery("customerByUuid", CustomerEntity.class).setParameter("uuid", uuid)
                    .getSingleResult();
        } catch(NoResultException nre) {
            return null;
        }
    }
}