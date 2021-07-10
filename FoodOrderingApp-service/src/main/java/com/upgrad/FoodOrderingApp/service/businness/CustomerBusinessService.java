package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Pattern;

@Service
public class CustomerBusinessService {

    @Autowired
    CustomerDao customerDao;

    @Autowired
    private PasswordCryptographyProvider cryptographyProvider;

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity signUpCustomer(CustomerEntity customerEntity)throws SignUpRestrictedException {
        // Regular expression for email format
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." + "[a-zA-Z0-9_+&*-]+)*@" + "(?:[a-zA-Z0-9-]+\\.)+[a-z" + "A-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);

        // Throws SignUpRestrictedException if any field is empty except lastname
        if (customerEntity.getFirstName() == null || customerEntity.getEmail() == null ||
                customerEntity.getContactNumber() == null || customerEntity.getPassword() == null) {
            throw new SignUpRestrictedException("SGR-005", "Except last name all fields should be filled");
            // Throws SignUpRestrictedException for invalid email format
        } else if (!pattern.matcher(customerEntity.getEmail()).matches()) {
            throw new SignUpRestrictedException("SGR-002", "Invalid email-id format!");
            // Throws SignUpRestrictedException if contactNumber is not numbers of length is not 10 digits
        } else if(!customerEntity.getContactNumber().matches("[0-9]+") || customerEntity.getContactNumber().length() != 10) {
            throw new SignUpRestrictedException("SGR-003", "Invalid contact number!");
            // Throws SignUpRestrictedException if password length is less than 8 characters
            // or if it does not contain at least one digit or if it does not contain at least one uppercase character
            // or if it does not contain any of the mentioned special characters
        } else if(customerEntity.getPassword().length() < 8
                || !customerEntity.getPassword().matches(".*[0-9]{1,}.*")
                || !customerEntity.getPassword().matches(".*[A-Z]{1,}.*")
                || !customerEntity.getPassword().matches(".*[#@$%&*!^]{1,}.*")) {
            throw new SignUpRestrictedException("SGR-004", "Weak password!");
            // Throws SignUpRestrictedException if a customer with the same contact number is already registered
        } else if (customerDao.getCustomerByContactNumber(customerEntity.getContactNumber()) != null) {
            throw new SignUpRestrictedException("SGR-001", "This contact number is already registered! Try other contact number.");
        }

        // Encryption of password
        String[] encryptedText = cryptographyProvider.encrypt(customerEntity.getPassword());
        customerEntity.setSalt(encryptedText[0]);
        customerEntity.setPassword(encryptedText[1]);

        // Called customerDao to insert new customer record in the database
        return customerDao.createCustomer(customerEntity);
    }
}