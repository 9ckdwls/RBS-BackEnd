package com.example.rbs.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.rbs.entity.Box;
import com.example.rbs.entity.Box.InstallStatus;

@Repository
public interface BoxRepository extends JpaRepository<Box, Integer> {

	// 수거함 이름으로 검색
	Optional<Box> findByName(String name);
	
	List<Box> findByInstallStatusIn(Collection<InstallStatus> statuses);
}
