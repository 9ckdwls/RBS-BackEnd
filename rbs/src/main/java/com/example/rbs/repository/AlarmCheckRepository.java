package com.example.rbs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.rbs.entity.AlarmCheck;
import com.example.rbs.entity.AlarmCheckId;

@Repository
public interface AlarmCheckRepository extends JpaRepository<AlarmCheck, AlarmCheckId> {

}
