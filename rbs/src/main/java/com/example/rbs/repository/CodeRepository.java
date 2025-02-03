package com.example.rbs.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.example.rbs.entity.Code;

@Repository
public interface CodeRepository extends JpaRepository<Code, Integer> {
	
	// 수거자 가입 코드 찾기
	@Query("select c from Code c")
	public Optional<Code> findCode();

}
