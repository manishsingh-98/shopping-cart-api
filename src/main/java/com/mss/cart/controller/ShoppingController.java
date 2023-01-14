package com.mss.cart.controller;

import com.mss.cart.config.UserContext;
import com.mss.cart.dto.ItemDto;
import com.mss.cart.service.ShoppingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shop")
public class ShoppingController {

    @Autowired
    private ShoppingService shoppingService;

    @GetMapping("/items")
    public List<ItemDto> getAvailableItems() {
        return shoppingService.getAvailableItems();
    }

    @GetMapping("/user-items")
    public List<ItemDto> getUserCartItems(UserContext userContext) {
        return shoppingService.getUserCartItem(userContext.getEmail());
    }

    @PutMapping("/add-cart-item")
    public List<ItemDto> addItemToCart(UserContext userContext, final String itemName) {
        return shoppingService.addItemToCart(userContext.getEmail(), itemName);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add-item")
    public ItemDto addItem(@Valid @RequestBody ItemDto itemDto) {
        return shoppingService.addItem(itemDto);
    }

    @DeleteMapping("/remove-item")
    public ResponseEntity<String> removeItem(UserContext userContext , @RequestParam String itemName) {
        shoppingService.removeItemFromCart(userContext.getEmail(), itemName);
        return  new ResponseEntity<>("item removed successfully", HttpStatus.OK);
    }
}
