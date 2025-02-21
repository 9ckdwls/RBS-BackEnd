package com.example.rbs.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.rbs.entity.Alarm;

@Repository
public interface AlarmRepository extends JpaRepository<Alarm, Integer> {

	// 미해결된 모든 알람
	@Query("SELECT a FROM Alarm a WHERE a.resolved = ?1 AND (a.role = ?2 OR a.role = ?3)")
	List<Alarm> findByResolvedAndRoles(boolean resolved, String role1, String role2);


	// boxId로 알람 찾기
	Optional<Alarm> findByBoxId(int id);

}
