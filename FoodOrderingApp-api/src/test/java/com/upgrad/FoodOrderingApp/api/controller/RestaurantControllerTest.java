package com.upgrad.FoodOrderingApp.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.upgrad.FoodOrderingApp.api.model.RestaurantList;
import com.upgrad.FoodOrderingApp.api.model.RestaurantListResponse;
import com.upgrad.FoodOrderingApp.service.businness.*;
import com.upgrad.FoodOrderingApp.service.entity.*;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.InvalidRatingException;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

import static com.upgrad.FoodOrderingApp.api.model.ItemQuantityResponseItem.TypeEnum.NON_VEG;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// This class contains all the test cases regarding the restaurant controller
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class RestaurantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RestaurantBusinessService mockRestaurantService;

    @MockBean
    private ItemService mockItemService;

    @MockBean
    private CategoryBusinessService mockCategoryService;

    @MockBean
    private CustomerBusinessService mockCustomerService;

    @MockBean
    private AddressService mockAddressService;

    // ------------------------------------------ GET /restaurant/{restaurant_id} ------------------------------------------

    //This test case passes when you get restaurant details based on restaurant id.
    @Test
    public void shouldGetRestaurantDetailsForCorrectRestaurantId() throws Exception {
        final RestaurantEntity restaurantEntity = getRestaurantEntity();
        when(mockRestaurantService.getRestaurantByUUId("someRestaurantId"))
                .thenReturn(restaurantEntity);

        final CategoryEntity categoryEntity = getCategoryEntity();
        when(mockCategoryService.getCategoryEntityByUuid("someRestaurantId"))
                .thenReturn(categoryEntity);

        final ItemEntity itemEntity = getItemEntity();
        when(mockItemService.getItemEntityByUuid( categoryEntity.getUuid()))
                .thenReturn(itemEntity);

        mockMvc
                .perform(get("/restaurant/someRestaurantId").contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(restaurantEntity.getUuid()))
                .andExpect(jsonPath("restaurant_name").value("Famous Restaurant"))
                .andExpect(jsonPath("customer_rating").value(3.4))
                .andExpect(jsonPath("number_customers_rated").value(200));
        verify(mockRestaurantService, times(1)).getRestaurantByUUId("someRestaurantId");
        verify(mockCategoryService, times(1)).getCategoryEntityByUuid("someRestaurantId");
        verify(mockItemService, times(1))
                .getItemEntityByUuid("someRestaurantId");
    }

    //This test case passes when you have handled the exception of trying to fetch any restaurant but your restaurant id
    // field is empty.
    @Test
    public void shouldNotGetRestaurantidIfRestaurantIdIsEmpty() throws Exception {
        when(mockRestaurantService.getRestaurantByUUId(anyString()))
                .thenThrow(new RestaurantNotFoundException("RNF-002", "Restaurant id field should not be empty"));

        mockMvc
                .perform(get("/restaurant/emptyString").contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("code").value("RNF-002"));
        verify(mockRestaurantService, times(1)).getRestaurantByUUId(anyString());
    }

    //This test case passes when you have handled the exception of trying to fetch restaurant details while there are
    // no restaurants with that restaurant id.
    @Test
    public void shouldNotGetRestaurantDetailsIfRestaurantNotFoundForGivenRestaurantId() throws Exception {
        when(mockRestaurantService.getRestaurantByUUId("someRestaurantId"))
                .thenThrow(new RestaurantNotFoundException("RNF-001", "No restaurant by this id"));

        mockMvc
                .perform(get("/restaurant/someRestaurantId").contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("code").value("RNF-001"));
        verify(mockRestaurantService, times(1)).getRestaurantByUUId("someRestaurantId");
        verify(mockCategoryService, times(0)).getCategoryEntityByUuid(anyString());
        verify(mockItemService, times(0)).getItemEntityByUuid(anyString());
    }

    // ------------------------------------------ GET /restaurant/name/{restaurant_name} ------------------------------------------

    //This test case passes when you are able to fetch restaurants by the name you provided.
    @Test
    public void shouldGetRestaurantDetailsByGivenName() throws Exception {
        final RestaurantEntity restaurantEntity = getRestaurantEntity();
        when(mockRestaurantService.getRestaurantsByName("someRestaurantName"))
                .thenReturn(Collections.singletonList(restaurantEntity));
        final AddressEntity addressEntity = new AddressEntity();
        addressEntity.setUuid("some-address-uuid");
        when(mockAddressService.getAddressById(anyLong())).thenReturn(addressEntity);

        final CategoryEntity categoryEntity = getCategoryEntity();
        when(mockCategoryService.getCategoryEntityByUuid(restaurantEntity.getUuid()))
                .thenReturn(categoryEntity);


        final String responseString = mockMvc
                .perform(get("/restaurant/name/someRestaurantName").contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        final RestaurantListResponse restaurantListResponse = new ObjectMapper().readValue(responseString, RestaurantListResponse.class);
        assertEquals(restaurantListResponse.getRestaurants().size(), 1);

        final RestaurantList restaurantList = restaurantListResponse.getRestaurants().get(0);
        assertEquals(restaurantList.getId().toString(), restaurantEntity.getUuid());
        assertEquals(restaurantList.getAddress().getId().toString(), restaurantEntity.getAddress().getUuid());
        assertEquals(restaurantList.getAddress().getState().getId().toString(), restaurantEntity.getAddress().getStateName().getUuid());

        verify(mockRestaurantService, times(1)).getRestaurantsByName("someRestaurantName");
    }

    //This test case passes when you have handled the exception of trying to fetch any restaurants but your restaurant name
    // field is empty.
    @Test
    public void shouldNotGetRestaurantByNameIfNameIsEmpty() throws Exception {
        when(mockRestaurantService.getRestaurantsByName(anyString()))
                .thenThrow(new RestaurantNotFoundException("RNF-003", "Restaurant name field should not be empty"));

        mockMvc
                .perform(get("/restaurant/name/emptyString").contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("code").value("RNF-003"));
        verify(mockRestaurantService, times(1)).getRestaurantsByName(anyString());
    }


    // ------------------------------------------ GET /restaurant/category/{category_id} ------------------------------------------

    //This test case passes when you are able to retrieve restaurant belonging to any particular categories.
    @Test
    public void shouldGetRestaurantDetailsByGivenCategoryId() throws Exception {
        final RestaurantCategoryEntity restaurantEntity = new RestaurantCategoryEntity();
        restaurantEntity.setRestaurant(getRestaurantEntity());
        ArrayList list = new ArrayList<RestaurantCategoryEntity>();
        list.add(restaurantEntity);
        when(mockRestaurantService.getRestaurantByCategoryId(anyLong()))
                .thenReturn(list);

        final CategoryEntity categoryEntity = getCategoryEntity();
        when(mockCategoryService.getCategoryEntityByUuid(anyString()))
                .thenReturn(categoryEntity);

        final String responseString = mockMvc
                .perform(get("/restaurant/category/someCategoryId").contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        final RestaurantList[] restaurantListResponse = new ObjectMapper().readValue(responseString, RestaurantList[].class);
        assertEquals(restaurantListResponse.length, 1);

        final RestaurantList restaurantList = restaurantListResponse[0];
        assertEquals(restaurantList.getId().toString(), restaurantEntity.getRestaurant().getUuid());
        assertEquals(restaurantList.getAddress().getId().toString(), restaurantEntity.getRestaurant().getAddress().getUuid());
        assertEquals(restaurantList.getAddress().getState().getId().toString(), restaurantEntity.getRestaurant().getAddress().getStateName().getUuid());

        verify(mockRestaurantService, times(1)).getRestaurantByCategoryId(anyLong());
        verify(mockCategoryService, times(1)).getCategoryEntityByUuid(anyString());
    }

    //This test case passes when you have handled the exception of trying to fetch any restaurants but your category id
    // field is empty.
    @Test
    public void shouldNotGetRestaurantByCategoryidIfCategoryIdIsEmpty() throws Exception {
        when(mockCategoryService.getCategoryEntityByUuid(anyString()))
                .thenThrow(new CategoryNotFoundException("CNF-001", "Category id field should not be empty"));

        mockMvc
                .perform(get("/restaurant/category/emptyString").contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("code").value("CNF-001"));
        verify(mockCategoryService, times(1)).getCategoryEntityByUuid(anyString());
    }

    //This test case passes when you have handled the exception of trying to fetch any restaurant by its category id, while there
    // is not category by that id in the database
    @Test
    public void shouldNotGetRestaurantsByCategoryIdIfCategoryDoesNotExistAgainstGivenId() throws Exception {
        when(mockCategoryService.getCategoryEntityByUuid(anyString())).thenReturn(getCategoryEntity());
        when(mockRestaurantService.getRestaurantByCategoryId(anyLong()))
                .thenThrow(new CategoryNotFoundException("CNF-002", "No category by this id"));

        mockMvc
                .perform(get("/restaurant/category/someCategoryId").contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("code").value("CNF-002"));
        verify(mockRestaurantService, times(1)).getRestaurantByCategoryId(anyLong());
    }


    // ------------------------------------------ GET /restaurant ------------------------------------------

    //This test case passes when you able to fetch the list of all restaurants.
    @Test
    public void shouldGetAllRestaurantDetails() throws Exception {
        final RestaurantEntity restaurantEntity = getRestaurantEntity();
        when(mockRestaurantService.getAllRestaurants())
                .thenReturn(Collections.singletonList(restaurantEntity));

        final CategoryEntity categoryEntity = getCategoryEntity();
        when(mockCategoryService.getCategoryEntityByUuid(restaurantEntity.getUuid()))
                .thenReturn(categoryEntity);

        final String responseString = mockMvc
                .perform(get("/restaurant").contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        final RestaurantListResponse restaurantListResponse = new ObjectMapper().readValue(responseString, RestaurantListResponse.class);
        assertEquals(restaurantListResponse.getRestaurants().size(), 1);

        final RestaurantList restaurantList = restaurantListResponse.getRestaurants().get(0);
        assertEquals(restaurantList.getId().toString(), restaurantEntity.getUuid());
        assertEquals(restaurantList.getAddress().getId().toString(), restaurantEntity.getAddress().getUuid());
        assertEquals(restaurantList.getAddress().getState().getId().toString(), restaurantEntity.getAddress().getStateName().getUuid());

        verify(mockRestaurantService, times(1)).getAllRestaurants();
    }


    // ------------------------------------------ PUT /restaurant/{restaurant_id} ------------------------------------------

    //This test case passes when you are able to update restaurant rating successfully.
    @Test
    public void shouldUpdateRestaurantRating() throws Exception {
        final String restaurantId = UUID.randomUUID().toString();

        CustomerEntity customerEntity = new CustomerEntity();
        when(mockCustomerService.getCustomer("database_accesstoken2"))
                .thenReturn(customerEntity);

        final RestaurantEntity restaurantEntity = getRestaurantEntity();
        restaurantEntity.setUuid(restaurantId);
        when(mockRestaurantService.getRestaurantByUUId(restaurantId)).thenReturn(restaurantEntity);


        when(mockRestaurantService.updateCustomerRating(4.5,restaurantId,customerEntity))
                .thenReturn(restaurantEntity);

        mockMvc
                .perform(put("/restaurant/" + restaurantId + "?customer_rating=4.5")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .header("authorization", "Bearer database_accesstoken2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(restaurantId));
        verify(mockCustomerService, times(1)).getCustomer("database_accesstoken2");
        verify(mockRestaurantService, times(1))
                .updateCustomerRating(4.5d,restaurantId,customerEntity);
    }

    //This test case passes when you have handled the exception of trying to update restaurant rating while you are
    // not logged in.
    @Test
    public void shouldNotUpdateRestaurantRatingIfCustomerIsNotLoggedIn() throws Exception {
        when(mockCustomerService.getCustomer("invalid_auth"))
                .thenThrow(new AuthorizationFailedException("ATHR-001", "Customer is not Logged in."));

        mockMvc
                .perform(put("/restaurant/someRestaurantId/?customer_rating=4.5")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .header("authorization", "Bearer invalid_auth"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("code").value("ATHR-001"));
        verify(mockCustomerService, times(1)).getCustomer("invalid_auth");
        verify(mockRestaurantService, times(0)).getRestaurantByUUId(anyString());
        verify(mockRestaurantService, times(0)).updateCustomerRating(anyDouble(),anyString(),any());
    }

    //This test case passes when you have handled the exception of trying to update restaurant rating while you are
    // already logged out.
    @Test
    public void shouldNotUpdateRestaurantRatingIfCustomerIsLoggedOut() throws Exception {
        when(mockCustomerService.getCustomer("invalid_auth"))
                .thenThrow(new AuthorizationFailedException("ATHR-002", "Customer is logged out. Log in again to access this endpoint."));

        mockMvc
                .perform(put("/restaurant/someRestaurantId/?customer_rating=4.5")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .header("authorization", "Bearer invalid_auth"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("code").value("ATHR-002"));
        verify(mockCustomerService, times(1)).getCustomer("invalid_auth");
        verify(mockRestaurantService, times(0)).getRestaurantByUUId(anyString());
        verify(mockRestaurantService, times(0)).updateCustomerRating(anyDouble(),anyString(),any());
    }

    //This test case passes when you have handled the exception of trying to update restaurant rating while your session
    // is already expired.
    @Test
    public void shouldNotUpdateRestaurantRatingIfCustomerSessionIsExpired() throws Exception {
        when(mockCustomerService.getCustomer("invalid_auth"))
                .thenThrow(new AuthorizationFailedException("ATHR-003", "Your session is expired. Log in again to access this endpoint."));

        mockMvc
                .perform(put("/restaurant/someRestaurantId/?customer_rating=4.5")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .header("authorization", "Bearer invalid_auth"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("code").value("ATHR-003"));
        verify(mockCustomerService, times(1)).getCustomer("invalid_auth");
        verify(mockRestaurantService, times(0)).getRestaurantByUUId(anyString());
        verify(mockRestaurantService, times(0)).updateCustomerRating(anyDouble(),anyString(),any());
    }

    //This test case passes when you have handled the exception of trying to update any restaurant but your restaurant id
    // field is empty.
    @Test
    public void shouldNotUpdateRestaurantIfRestaurantIdIsEmpty() throws Exception {
        when(mockCustomerService.getCustomer("database_accesstoken2"))
                .thenReturn(new CustomerEntity());

        when(mockRestaurantService.getRestaurantByUUId(anyString()))
                .thenThrow(new RestaurantNotFoundException("RNF-002", "Restaurant id field should not be empty"));

        mockMvc
                .perform(get("/restaurant/emptyString").contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .header("authorization", "Bearer database_accesstoken2"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("code").value("RNF-002"));
        verify(mockCustomerService, times(0)).getCustomer("database_accesstoken2");
        verify(mockRestaurantService, times(1)).getRestaurantByUUId(anyString());
    }

    //This test case passes when you have handled the exception of trying to update restaurant rating while the
    // restaurant id you provided does not exist in the database.
    @Test
    public void shouldNotUpdateRestaurantRatingIfRestaurantDoesNotExists() throws Exception {
        final String restaurantId = UUID.randomUUID().toString();

        when(mockCustomerService.getCustomer("database_accesstoken2"))
                .thenReturn(new CustomerEntity());

        when(mockRestaurantService.getRestaurantEntity(restaurantId))
                .thenThrow(new RestaurantNotFoundException("RNF-001", "No restaurant by this id"));

        mockMvc
                .perform(put("/restaurant/" + restaurantId + "?customer_rating=4.5")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .header("authorization", "Bearer database_accesstoken2"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("code").value("RNF-001"));
        verify(mockCustomerService, times(1)).getCustomer("database_accesstoken2");
        verify(mockRestaurantService, times(1)).getRestaurantEntity(restaurantId);
        verify(mockRestaurantService, times(0))
                .updateCustomerRating(anyDouble(),anyString(), any());
    }

    //This test case passes when you have handled the exception of trying to update restaurant rating while the rating
    // you provided is less than 1.
    @Test
    public void shouldNotUpdateRestaurantRatingIfNewRatingIsLessThan1() throws Exception {
        final String restaurantId = UUID.randomUUID().toString();

        CustomerEntity customerEntity = new CustomerEntity();
        when(mockCustomerService.getCustomer("database_accesstoken2"))
                .thenReturn(customerEntity);

        final RestaurantEntity restaurantEntity = getRestaurantEntity();
        when(mockRestaurantService.getRestaurantByUUId(restaurantId)).thenReturn(restaurantEntity);

        when(mockRestaurantService.updateCustomerRating(-5.5d,restaurantId,customerEntity))
                .thenThrow(new InvalidRatingException("IRE-001", "Rating should be in the range of 1 to 5"));

        mockMvc
                .perform(put("/restaurant/" + restaurantId + "?customer_rating=-5.5")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .header("authorization", "Bearer database_accesstoken2"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value("IRE-001"));
        verify(mockCustomerService, times(1)).getCustomer("database_accesstoken2");
        verify(mockRestaurantService, times(1))
                .updateCustomerRating(-5.5d,restaurantId,customerEntity);
    }

    //This test case passes when you have handled the exception of trying to update restaurant rating while the rating
    // you provided is greater than 5.
    @Test
    public void shouldNotUpdateRestaurantRatingIfNewRatingIsGreaterThan5() throws Exception {
        final String restaurantId = UUID.randomUUID().toString();

        CustomerEntity customerEntity = new CustomerEntity();
        when(mockCustomerService.getCustomer("database_accesstoken2"))
                .thenReturn(customerEntity);

        final RestaurantEntity restaurantEntity = getRestaurantEntity();
        when(mockRestaurantService.getRestaurantByUUId(restaurantId)).thenReturn(restaurantEntity);

        when(mockRestaurantService.updateCustomerRating(5.5d,restaurantId,customerEntity))
                .thenThrow(new InvalidRatingException("IRE-001", "Rating should be in the range of 1 to 5"));

        mockMvc
                .perform(put("/restaurant/" + restaurantId + "?customer_rating=5.5")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .header("authorization", "Bearer database_accesstoken2"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value("IRE-001"));
        verify(mockCustomerService, times(1)).getCustomer("database_accesstoken2");
        verify(mockRestaurantService, times(1))
                .updateCustomerRating(5.5d, restaurantId,customerEntity);
    }

    // ------------------------------------------ POJO builders ------------------------------------------

    private ItemEntity getItemEntity() {
        final ItemEntity itemEntity = new ItemEntity();
        final String itemId = UUID.randomUUID().toString();
        itemEntity.setUuid(itemId);
        itemEntity.setitemName("someItem");
        itemEntity.setType(NON_VEG.toString());
        itemEntity.setPrice(200);
        return itemEntity;
    }

    private CategoryEntity getCategoryEntity() {
        final CategoryEntity categoryEntity = new CategoryEntity();
        final String categoryId = UUID.randomUUID().toString();
        categoryEntity.setUuid(categoryId);
        categoryEntity.setCategoryName("someCategory");
        categoryEntity.setId(1234l);
        return categoryEntity;
    }

    private RestaurantEntity getRestaurantEntity() {
        final String stateId = UUID.randomUUID().toString();
        final StateEntity stateEntity = new StateEntity(stateId, "someState");
        final String addressId = UUID.randomUUID().toString();
        final AddressEntity addressEntity = new AddressEntity(addressId, "a/b/c", "someLocality", "someCity", "100000", stateEntity);

        final RestaurantEntity restaurantEntity = new RestaurantEntity();
        final String restaurantId = UUID.randomUUID().toString();
        restaurantEntity.setUuid(restaurantId);
        restaurantEntity.setAddress(addressEntity);
        restaurantEntity.setAveragePriceForTwo(123);
        restaurantEntity.setCustomerRating(new BigDecimal(3.4));
        restaurantEntity.setNumberOfCustomerRated(200);
        restaurantEntity.setPhotoURL("someurl");
        restaurantEntity.setRestaurantName("Famous Restaurant");
        return restaurantEntity;
    }
}