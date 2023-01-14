package com.mss.cart.service;

import com.mss.cart.dto.ItemDto;
import com.mss.cart.entity.Cart;
import com.mss.cart.entity.CartItem;
import com.mss.cart.entity.Item;
import com.mss.cart.entity.User;
import com.mss.cart.exception.ErrorResponseException;
import com.mss.cart.repository.CartItemRepository;
import com.mss.cart.repository.CartRepository;
import com.mss.cart.repository.ItemRepository;
import com.mss.cart.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ShoppingServiceImpl implements ShoppingService {

    private final CartRepository cartRepository;

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    private final CartItemRepository cartItemRepository;

    @Autowired
    public ShoppingServiceImpl(CartRepository cartRepository, ItemRepository itemRepository, UserRepository userRepository, CartItemRepository cartItemRepository) {
        this.cartRepository = cartRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.cartItemRepository = cartItemRepository;
    }

    @Override
    public List<ItemDto> getUserCartItem(String username) {
        List<CartItem> cartItems = cartItemRepository.findAllByUserUsername(username);
        return cartItems.stream().map(x -> new ItemDto(x.getItem().getName(), x.getItem().getPrice(), x.getQuantity())).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getAvailableItems() {
        List<Item> itemList = itemRepository.findAllByQuantityNot(0);

        return itemList.stream().map(x -> new ItemDto(x.getName(), x.getPrice(), null)).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> addItemToCart(String username, String itemName) {

        Optional<Item> itemOptional = itemRepository.findByNameAndQuantityNot(itemName, 0L);

        if (itemOptional.isEmpty()) {
            throw new ErrorResponseException("no such item found, please provide a valid item name");
        }

        Optional<Cart> cartOptional = cartRepository.findByUserUsername(username);

        Cart cart;
        Item item = itemOptional.get();
        if (cartOptional.isPresent()) {
            cart = cartOptional.get();

            Optional<CartItem> cartItemOptional = cartItemRepository.findByItemNameAndUserUsername(itemName, username);
            CartItem cartItem;
            if (cartItemOptional.isPresent()) {
                cartItem = cartItemOptional.get();
                cartItem.setQuantity(cartItem.getQuantity() + 1);
            } else {
                cartItem = CartItem.builder().item(item).quantity(1L).user(cart.getUser()).build();
            }
            cart.getCartItems().add(cartItem);
        } else {
            Optional<User> userOptional = userRepository.findByUsername(username);

            if (userOptional.isEmpty()) {
                throw new ErrorResponseException("user not found");
            }

            cart = new Cart();
            item = itemOptional.get();
            cart.setUser(userOptional.get());
            cart.setCartItems(Set.of(CartItem.builder().item(item).quantity(1L).user(userOptional.get()).build()));
        }

        item.setQuantity(item.getQuantity() - 1);

        cartRepository.save(cart);

        itemRepository.save(item);

        return cart.getCartItems().stream().map(x -> new ItemDto(x.getItem().getName(), x.getItem().getPrice(), x.getQuantity())).collect(Collectors.toList());

    }

    @Override
    public ItemDto addItem(ItemDto itemDto) {

        Optional<Item> itemOptional = itemRepository.findByNameAndQuantityNot(itemDto.getName(), -1L);

        Item item;

        if (itemOptional.isPresent()) {
            item = itemOptional.get();
            item.setQuantity(item.getQuantity() + itemDto.getQuantity());
        } else {
            item = Item.builder()
                    .name(itemDto.getName())
                    .price(itemDto.getPrice())
                    .quantity(itemDto.getQuantity()).build();
        }
        itemRepository.save(item);

        itemDto.setQuantity(item.getQuantity());

        return itemDto;
    }

    @Override
    public void removeItemFromCart(String username, String itemName) {
        Optional<CartItem> carItemOptional = cartItemRepository.findByItemNameAndUserUsername(itemName, username);
        if (carItemOptional.isPresent()) {
            Item item = carItemOptional.get().getItem();
            item.setQuantity(item.getQuantity() + carItemOptional.get().getQuantity());
            cartItemRepository.delete(carItemOptional.get());
            itemRepository.save(item);
        }
    }
}
