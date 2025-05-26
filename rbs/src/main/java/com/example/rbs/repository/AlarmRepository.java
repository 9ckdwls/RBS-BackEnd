package com.example.rbs.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.rbs.entity.Alarm;
import com.example.rbs.entity.Alarm.AlarmStatus;

@Repository
public interface AlarmRepository extends JpaRepository<Alarm, Integer> {

	@Query("""
		      SELECT a
		        FROM Alarm a
		       WHERE a.type IN ('FIRE', 'FIRE_IN_PROGRESS', 'FIRE_COMPLETED')
		         AND a.targetUserId = :targetUserId
		         AND a.resolved = 'RESOLVED'
		      """)
		    List<Alarm> findResolvedFireLogsByTargetUser(@Param("targetUserId") String targetUserId);
}
