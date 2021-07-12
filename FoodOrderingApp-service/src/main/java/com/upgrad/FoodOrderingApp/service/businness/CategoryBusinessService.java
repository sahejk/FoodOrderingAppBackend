package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CategoryDao;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryBusinessService {
    @Autowired
    private CategoryDao categoryDao;

    // A Method which takes the categoryUUId as parameter for  getCategoryEntityByUUId endpoint
    public CategoryEntity getCategoryEntityByUuid(final String categoryUUId) throws CategoryNotFoundException {
        // Throw exception if path variable(category_id) is empty
        if(categoryUUId == null || categoryUUId.isEmpty() || categoryUUId.equalsIgnoreCase("\"\"")){
            throw new CategoryNotFoundException("CNF-001", "Category id field should not be empty");
        }
        CategoryEntity matchedCategory = categoryDao.getCategoryByUUId(categoryUUId);
        // Throw exception if given category_id not matched with any category in DB
        if(matchedCategory == null){
            throw new CategoryNotFoundException("CNF-002", "No category by this id");
        }

        return matchedCategory;
    }

    // A Method which is for  getAllCategories endpoint
    public List<CategoryEntity> getAllCategories(){
        return  categoryDao.getAllCategories();
    }
}
