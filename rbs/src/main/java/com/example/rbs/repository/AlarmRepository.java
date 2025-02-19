package com.example.rbs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.rbs.entity.Alarm;

@Repository
public interface AlarmRepository extends JpaRepository<Alarm, Integer> {

}
