package com.example.rbs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.rbs.entity.Box;
import com.example.rbs.entity.BoxId;


@Repository
public interface BoxRepository extends JpaRepository<Box, BoxId> {

}
