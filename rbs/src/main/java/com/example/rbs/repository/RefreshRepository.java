package com.example.rbs.repository;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.rbs.entity.Refresh;

@Repository
public interface RefreshRepository extends JpaRepository<Refresh, Long> {
	
	boolean existsByRefresh(String refresh);
	
	@Transactional
	void deleteByRefresh(String refresh);
	
	@Transactional
	void deleteByExpirationBefore(Date now);
}
