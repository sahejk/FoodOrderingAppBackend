package com.upgrad.FoodOrderingApp.api.controller;


import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.CustomerBusinessService;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import com.upgrad.FoodOrderingApp.service.exception.UpdateCustomerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
import java.util.UUID;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    CustomerBusinessService customerBusinessService;

    @RequestMapping(method = RequestMethod.POST,path = "/",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupCustomerResponse> signUpCustomer(SignupCustomerRequest signupCustomerRequest)throws SignUpRestrictedException {
        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setFirstName(signupCustomerRequest.getFirstName());
        customerEntity.setLastName(signupCustomerRequest.getLastName());
        customerEntity.setContactNumber(signupCustomerRequest.getContactNumber());
        customerEntity.setEmail(signupCustomerRequest.getEmailAddress());
        customerEntity.setPassword(signupCustomerRequest.getPassword());
        customerEntity.setUuid(UUID.randomUUID().toString());


        CustomerEntity signedUpCustomer =  customerBusinessService.signUpCustomer(customerEntity);
        SignupCustomerResponse signupCustomerResponse = new SignupCustomerResponse().id(signedUpCustomer.getUuid()).status("CUSTOMER SUCCESSFULLY REGISTERED");

        return new ResponseEntity<>(signupCustomerResponse, HttpStatus.CREATED);
    }

    // Login endpoint requests for Basic authentication of the customer and logs in a customer successfully.
    @RequestMapping(method = RequestMethod.POST , path="/customer/login" ,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<LoginResponse> login(@RequestHeader("authorization")  final String authorization)
            throws AuthenticationFailedException {

        // Basic authentication format validation
        if (authorization == null || !authorization.startsWith("Basic ")) {
            throw new AuthenticationFailedException("ATH-003", "Incorrect format of decoded customer name and password");
        }

        // Gets the contactNumber:password after base64 decoding
        byte[] decode = Base64.getDecoder().decode(authorization.split("Basic ")[1]);
        String decodedText = new String(decode);

        // Validation to check whether the format is contactNumber:password
        if (!decodedText.matches("([0-9]+):(.+?)")) {
            throw new AuthenticationFailedException("ATH-003", "Incorrect format of decoded customer name and password");
        }

        // Splits contactNumber:password text to seperate array elements
        String[] decodedArray = decodedText.split(":");

        // Authenticates the username and password and gets the customer auth token
        CustomerAuthEntity customerAuthToken = customerBusinessService.authenticate(decodedArray[0] , decodedArray[1]);

        // Gets the customer details based on auth token
        CustomerEntity customer = customerAuthToken.getCustomer();

        // Loads the LoginResponse with the uuid, firstName, lastName, email and contactNumber of the logged in customer
        // and the respective status message
        LoginResponse loginResponse = new LoginResponse().id(customer.getUuid()).firstName(customer.getFirstName())
                .lastName(customer.getLastName()).emailAddress(customer.getEmail()).contactNumber(customer.getContactNumber())
                .message("LOGGED IN SUCCESSFULLY");

        // Loads the http headers with access token and access-control-expose-headers
        HttpHeaders headers = new HttpHeaders();
        headers.add("access-token", customerAuthToken.getAccessToken());
        headers.add("access-control-expose-headers", "access-token");

        // Returns the LoginResponse with OK http status
        return new ResponseEntity<LoginResponse>(loginResponse, headers, HttpStatus.OK);
    }

    // Logout endpoint requests for Bearer authorization of the customer and logs out the customer successfully.
    @RequestMapping(method= RequestMethod.POST, path="/customer/logout", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<LogoutResponse>logout(@RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException {

        // Splits the Bearer authorization text as Bearer and bearerToken
        String[] bearerToken = authorization.split( "Bearer ");

        // Calls the logout method by passing the bearer token
        final CustomerAuthEntity customerAuthTokenEntity = customerBusinessService.logout(bearerToken[1]);

        // Gets the details of the customer based on received access token
        CustomerEntity customerEntity = customerAuthTokenEntity.getCustomer();

        // Loads the LogoutResponse with uuid of the logged out customer and the respective status message
        LogoutResponse logoutResponse = new LogoutResponse().id(customerEntity.getUuid()).message("LOGGED OUT SUCCESSFULLY");

        // Returns the LogoutResponse with OK http status
        return new ResponseEntity<LogoutResponse>(logoutResponse, HttpStatus.OK);
    }

    // Update customer endpoint requests for firstname and lastname of the customer in “UpdateCustomerRequest”
    // and updates the customer details successfully.
    @RequestMapping(method = RequestMethod.PUT, path = "/customer",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UpdateCustomerResponse> updateCustomer(final UpdateCustomerRequest customerUpdateRequest,
                                                                 @RequestHeader("authorization") final String authorizaton)
            throws AuthorizationFailedException, UpdateCustomerException {

        // Splits the Bearer authorization text as Bearer and bearerToken
        String[] bearerToken = authorizaton.split("Bearer ");

        //Creating a new instance of the CustomerEntity
        final CustomerEntity updatedCustomerEntity = new CustomerEntity();

        // Gets the firstname and lastname from the UpdateCustomerRequest
        updatedCustomerEntity.setFirstName(customerUpdateRequest.getFirstName());
        updatedCustomerEntity.setLastName(customerUpdateRequest.getLastName());

        // Calls the updateCustomer method to update the firstname and/or lastname of the customer
        CustomerEntity customerEntity = customerBusinessService.updateCustomer(updatedCustomerEntity, bearerToken[1]);

        // Loads the UpdateCustomerResponse with uuid, firstname and lastname of the updated customer
        // and the respective status message
        UpdateCustomerResponse updateCustomerResponse = new UpdateCustomerResponse().id(customerEntity.getUuid())
                .firstName(customerEntity.getFirstName()).lastName(customerEntity.getLastName())
                .status("CUSTOMER DETAILS UPDATED SUCCESSFULLY");

        // Returns the UpdateCustomerResponse with OK http status
        return new ResponseEntity<UpdateCustomerResponse>(updateCustomerResponse, HttpStatus.OK);

    }

    // Update customer password endpoint requests for old and new password of the customer in “UpdatePasswordRequest”
    // and updates the customer password successfully.
    @RequestMapping(method = RequestMethod.PUT, path = "/customer/password",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UpdatePasswordResponse> updateCustomerPassword(final UpdatePasswordRequest customerUpdatePasswordRequest,
                                                                         @RequestHeader("authorization") final String authorizaton)
            throws AuthorizationFailedException, UpdateCustomerException {

        // Splits the Bearer authorization text as Bearer and bearerToken
        String[] bearerToken = authorizaton.split("Bearer ");

        // Gets the old and new password from UpdatePasswordRequest
        String oldPassword = customerUpdatePasswordRequest.getOldPassword();
        String newPassword = customerUpdatePasswordRequest.getNewPassword();

        // Calls the updateCustomerPassword method to update the password of the customer
        CustomerEntity customerEntity = customerBusinessService.updateCustomerPassword(oldPassword, newPassword, bearerToken[1]);

        // Loads the UpdatePasswordResponse with uuid of the logged in customer
        // and the respective status message
        UpdatePasswordResponse updatePasswordResponse = new UpdatePasswordResponse().id(customerEntity.getUuid())
                .status("CUSTOMER PASSWORD UPDATED SUCCESSFULLY");

        // Returns the UpdatePasswordResponse with OK http status
        return new ResponseEntity<UpdatePasswordResponse>(updatePasswordResponse, HttpStatus.OK);

    }

}