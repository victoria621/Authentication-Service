package com.innowise.authenticationservice.repository;

import com.innowise.authenticationservice.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}