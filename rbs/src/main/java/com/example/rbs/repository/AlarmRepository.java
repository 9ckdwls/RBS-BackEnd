package com.example.rbs.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.rbs.entity.Alarm;

@Repository
public interface AlarmRepository extends JpaRepository<Alarm, Integer> {

	// 미해결된 모든 알람
	@Query("SELECT a FROM Alarm a WHERE (a.role = :role OR a.role = 'ROLE_ALL') "
			+ "AND (a.resolved = 'UNRESOLVED' OR (a.resolved = 'IN_PROGRESS' AND a.userId = :userId))")
	List<Alarm> findRelevantAlarms(@Param("role") String role, @Param("userId") String userId);

	// boxId로 알람 찾기
	Optional<Alarm> findByBoxId(int id);

}
