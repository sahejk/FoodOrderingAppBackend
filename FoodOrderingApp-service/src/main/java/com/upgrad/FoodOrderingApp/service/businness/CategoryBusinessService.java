package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CategoryDao;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryBusinessService {
    @Autowired
    private CategoryDao categoryDao;

    // A Method which takes the categoryUUId as parameter for  getCategoryEntityByUUId endpoint
    public CategoryEntity getCategoryEntityByUuid(final String categoryUUId){
        return  categoryDao.getCategoryByUUId(categoryUUId);
    }
}
