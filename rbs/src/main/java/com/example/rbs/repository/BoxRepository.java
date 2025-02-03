package com.example.rbs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.rbs.entity.Box;


@Repository
public interface BoxRepository extends JpaRepository<Box, Integer> {

}
