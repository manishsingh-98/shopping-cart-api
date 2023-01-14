package com.mss.cart;

import com.mss.cart.dto.CreateUserRequestDto;
import com.mss.cart.dto.ItemDto;
import com.mss.cart.entity.Item;
import com.mss.cart.enums.Role;
import com.mss.cart.repository.ItemRepository;
import com.mss.cart.repository.UserRepository;
import com.mss.cart.service.ShoppingService;
import com.mss.cart.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CartApplicationTests {

	private final UserService userService;

	private final ShoppingService shoppingService;

	private final UserRepository userRepository;

	private final ItemRepository itemRepository;

	@Autowired
	public CartApplicationTests(UserService userService, ShoppingService shoppingService, UserRepository userRepository, ItemRepository itemRepository) {
		this.userService = userService;
		this.shoppingService = shoppingService;
		this.userRepository = userRepository;
		this.itemRepository = itemRepository;
	}

	@Test
	void test_user_creation() {
		CreateUserRequestDto createUserRequestDto = new CreateUserRequestDto("testuser", "pass", Role.USER);

		userService.createUser(createUserRequestDto);

		assertTrue(userRepository.existsByUsername("testuser"));

	}


	@Test
	void test_user_add_cart_item() {
		CreateUserRequestDto createUserRequestDto = new CreateUserRequestDto("testuser1", "pass", Role.USER);

		userService.createUser(createUserRequestDto);

		ItemDto itemDto = new ItemDto("test", 1.2, 1L);

		shoppingService.addItem(itemDto);

		final var itemList =  shoppingService.addItemToCart("testuser1", "test");

		assertEquals(itemList.size(), 1);

		Optional<Item> itemOptional = itemRepository.findByNameAndQuantityNot("test", -1L);

		assertTrue(itemOptional.isPresent());

		assertEquals(itemOptional.get().getQuantity(), 0);

		final var userItemList = shoppingService.getUserCartItem("testuser1");

		assertFalse(userItemList.isEmpty());
	}

}
