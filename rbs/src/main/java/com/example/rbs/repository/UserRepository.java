package com.example.rbs.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.rbs.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, String>{
	
	// 해당 id 존재하는지
	boolean existsByIdOrPhoneNumber(String id, String phoneNumber);
	
	// id로 User 찾기
	Optional<User> findById(String id);
}
