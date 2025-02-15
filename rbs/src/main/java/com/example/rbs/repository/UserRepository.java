package com.example.rbs.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.rbs.entity.BoxLog;
import com.example.rbs.entity.User;


public interface UserRepository extends JpaRepository<User, String>{
	
	// 해당 id 존재하는지
	boolean existsByIdAndPhoneNumber(String id, String phoneNumber);
	
	// id로 User 찾기
	Optional<User> findById(String id);
	
	// userId로 boxLog 찾기
	List<BoxLog> findByUserId(String id);
}
