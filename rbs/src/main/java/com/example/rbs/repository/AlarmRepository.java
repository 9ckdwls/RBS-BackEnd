package com.example.rbs.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.rbs.entity.Alarm;
import com.example.rbs.entity.Alarm.AlarmStatus;

@Repository
public interface AlarmRepository extends JpaRepository<Alarm, Integer> {

	// 화재처리 내역 보기
	List<Alarm> findByResolvedAndTargetUserId(AlarmStatus resolved, String targetUserId);
}
