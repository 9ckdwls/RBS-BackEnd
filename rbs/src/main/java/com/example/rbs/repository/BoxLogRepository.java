package com.example.rbs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.rbs.entity.BoxLog;

@Repository
public interface BoxLogRepository extends JpaRepository<BoxLog, Integer> {
	
}
