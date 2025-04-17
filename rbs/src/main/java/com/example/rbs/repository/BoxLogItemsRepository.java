package com.example.rbs.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.rbs.entity.BoxLogItems;

@Repository
public interface BoxLogItemsRepository extends JpaRepository<BoxLogItems, Integer>{

	// 수거로그 아이디로 아이템 가져오기
	List<BoxLogItems> findByBoxLogId(int id);

}
