package com.mss.cart.service;

import com.mss.cart.dto.ItemDto;

import java.util.List;

public interface ShoppingService {
    List<ItemDto> getUserCartItem(String username);

    List<ItemDto> getAvailableItems();

    List<ItemDto> addItemToCart(String username, String itemName);

    ItemDto addItem(ItemDto itemDto);

    void removeItemFromCart(String username, String itemName);
}
