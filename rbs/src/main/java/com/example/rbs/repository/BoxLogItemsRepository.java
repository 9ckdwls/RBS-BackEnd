package com.example.rbs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.rbs.entity.BoxLogItems;

@Repository
public interface BoxLogItemsRepository extends JpaRepository<BoxLogItems, Integer>{

}
