package com.example.rbs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.rbs.entity.Code;

@Repository
public interface CodeRepository extends JpaRepository<Code, Integer> {

}
