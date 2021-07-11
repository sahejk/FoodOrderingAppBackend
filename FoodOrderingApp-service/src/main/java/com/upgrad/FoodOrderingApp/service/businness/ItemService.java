package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.ItemDao;
import com.upgrad.FoodOrderingApp.service.dao.OrderItemDao;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrdersEntity;
import com.upgrad.FoodOrderingApp.service.exception.ItemNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class ItemService {
    @Autowired
    private OrderItemDao orderItemDao;

    @Autowired
    private ItemDao itemDao;

    public List<OrderItemEntity> getItemsByOrder(OrdersEntity orderEntity) {
        return orderItemDao.getItemsByOrder(orderEntity);
    }

    @Transactional
    // A Method which takes the item uuid as parameter for getItemEntityByUuid
    public ItemEntity getItemEntityByUuid(final String itemUuid) throws ItemNotFoundException {

        ItemEntity itemEntity = itemDao.getItemByUuid(itemUuid);
        if (itemEntity == null) {
            throw new ItemNotFoundException("INF-003", "No item by this id exist");
        } else {
            return itemEntity;
        }
    }
}
