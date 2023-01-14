package com.mss.cart.repository;

import com.mss.cart.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    Optional<Item> findByNameAndQuantityNot(String itemName, Long quantity);

    List<Item> findAllByQuantityNot(long quantity);
}
