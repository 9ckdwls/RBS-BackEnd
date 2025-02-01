package com.example.rbs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.rbs.entity.BoxLog;
import com.example.rbs.entity.BoxLogId;

@Repository
public interface BoxLogRepository extends JpaRepository<BoxLog, BoxLogId> {

}
