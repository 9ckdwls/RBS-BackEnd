package com.example.rbs.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.rbs.entity.BoxLog;
import com.example.rbs.entity.BoxLogId;

@Repository
public interface BoxLogRepository extends JpaRepository<BoxLog, BoxLogId> {
	
	// 모든 수거함로그 조회
	List<BoxLog> findAll();
	
	// userId로 수거함로그 검색
	List<BoxLog> findByBoxLogId_UserId(String userId);
}
