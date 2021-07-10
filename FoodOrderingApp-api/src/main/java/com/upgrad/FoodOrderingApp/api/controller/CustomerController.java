package com.upgrad.FoodOrderingApp.api.controller;


import com.upgrad.FoodOrderingApp.api.model.LoginResponse;
import com.upgrad.FoodOrderingApp.api.model.LogoutResponse;
import com.upgrad.FoodOrderingApp.api.model.SignupCustomerRequest;
import com.upgrad.FoodOrderingApp.api.model.SignupCustomerResponse;
import com.upgrad.FoodOrderingApp.service.businness.CustomerBusinessService;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
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

}