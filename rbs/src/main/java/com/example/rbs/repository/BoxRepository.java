package com.example.rbs.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.rbs.entity.Box;
import com.example.rbs.entity.BoxId;


@Repository
public interface BoxRepository extends JpaRepository<Box, BoxId> {

	// 수거함 이름으로 검색
	Optional<Box> findByBoxId_name(String name);

}
