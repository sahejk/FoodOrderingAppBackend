package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.AddressDao;
import com.upgrad.FoodOrderingApp.service.dao.CustomerAddressDao;
import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.dao.StateDao;
import com.upgrad.FoodOrderingApp.service.entity.*;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SaveAddressException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class AddressService {
    @Autowired
    private StateDao stateDao;

    @Autowired
    private  CustomerBusinessService customerBusinessService;

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private CustomerAddressDao customerAddressDao;

    @Autowired
    private AddressDao addressDao;

    @Transactional
    public StateEntity getStateByUUID(String stateUuid) throws SaveAddressException, AddressNotFoundException {
        StateEntity stateEntity = stateDao.getStateByUuid(stateUuid);
        if(stateUuid.isEmpty()){
            throw new SaveAddressException("SAR-001", "No field can be empty");
        }
        else if(stateEntity == null){
            throw new AddressNotFoundException("ANF-002", "No state by this id");
        } else {
            return stateEntity;
        }

    }

    @Transactional
    public AddressEntity saveAddress(AddressEntity addressEntity, String bearerToken)
            throws AuthorizationFailedException, SaveAddressException, AddressNotFoundException {

        customerBusinessService.validateAccessToken(bearerToken);

        getStateByUUID(addressEntity.getStateName().getUuid());

        if (addressEntity.getCity() == null || addressEntity.getCity().isEmpty() ||
                addressEntity.getStateName() == null ||
                //addressEntity.getState() == null ||
                addressEntity.getFlatBuilNumber() == null || addressEntity.getFlatBuilNumber().isEmpty() ||
                addressEntity.getLocality() == null || addressEntity.getLocality().isEmpty() ||
                addressEntity.getPincode() == null || addressEntity.getPincode().isEmpty() ||
                addressEntity.getUuid() == null || addressEntity.getUuid().isEmpty()) {
            throw new SaveAddressException("SAR-001", "No field can be empty.");
        }

        /*if (stateDao.getStateByUuid(addressEntity.getState().getUuid()) == null) {
            throw new AddressNotFoundException("ANF-002", "No state by this id");
        }*/

        if (stateDao.getStateById(addressEntity.getStateName().getId()) == null) {
            throw new AddressNotFoundException("ANF-002", "No state by this id.");
        }

        if (!addressEntity.getPincode().matches("^[1-9][0-9]{5}$")) {
            throw new SaveAddressException("SAR-002", "Invalid pincode");
        }

        addressEntity = addressDao.createAddress(addressEntity);

        //get the customerAuthToken details from customerDao
        CustomerAuthEntity customerAuthTokenEntity = customerDao.getCustomerAuthToken(bearerToken);

        // Save the customer address
        final CustomerEntity customerEntity = customerDao.getCustomerByUuid(customerAuthTokenEntity.getUuid());
        final CustomerAddressEntity customerAddressEntity = new CustomerAddressEntity();

        customerAddressEntity.setAddress(addressEntity);
        customerAddressEntity.setCustomer(customerEntity);
        customerAddressDao.createCustomerAddress(customerAddressEntity);

        return addressEntity;
    }

    public List<AddressEntity> getAllAddress(final String bearerToken) throws AuthorizationFailedException {

        customerBusinessService.validateAccessToken(bearerToken);

        //get the customerAuthToken details from customerDao
        CustomerAuthEntity customerAuthTokenEntity = customerDao.getCustomerAuthToken(bearerToken);

        return customerAddressDao.getAddressForCustomerByUuid(customerAuthTokenEntity.getCustomer().getUuid());
    }

    @Transactional
    public StateEntity getStateById(Long stateId) {
        return stateDao.getStateById(stateId);
    }

    @Transactional
    public AddressEntity deleteAddress(String addressUuid, String bearerToken)
            throws AuthorizationFailedException, AddressNotFoundException {

        customerBusinessService.validateAccessToken(bearerToken);
        CustomerAuthEntity customerAuthTokenEntity = customerDao.getCustomerAuthToken(bearerToken);

        if (addressUuid == null) {
            throw new AddressNotFoundException("ANF-005", "Address id can not be empty.");
        }

        AddressEntity addressEntity = addressDao.getAddressByUuid(addressUuid);
        CustomerAddressEntity customerAddressEntity = customerAddressDao.getCustAddressByCustIdAddressId(customerAuthTokenEntity.getCustomer(), addressEntity);

        if (addressEntity == null) {
            throw new AddressNotFoundException("ANF-003", "No address by this id.");
        } else if (customerAddressEntity == null) {
            throw new AuthorizationFailedException("ATHR-004", "You are not authorized to view/update/delete any one else's address");
        }

        return addressDao.deleteAddressByUuid(addressEntity);
    }

    public List<StateEntity> getAllStates() throws AuthorizationFailedException {
        return stateDao.getAllStates();
    }}
