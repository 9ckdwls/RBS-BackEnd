package com.example.rbs.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.rbs.entity.Alarm;

@Repository
public interface AlarmRepository extends JpaRepository<Alarm, Integer> {

	List<Alarm> findByResolved(boolean b);

}
