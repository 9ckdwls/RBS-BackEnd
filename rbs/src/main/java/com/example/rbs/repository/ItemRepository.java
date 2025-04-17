package com.example.rbs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.rbs.entity.Item;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer>{

}
