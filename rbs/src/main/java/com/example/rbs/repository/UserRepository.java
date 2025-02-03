package com.example.rbs.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.rbs.entity.User;

public interface UserRepository extends JpaRepository<User, String> {

	// id가 존재하는지
	boolean existsById(String id);

	// id로 User 찾기
	Optional<User> findById(String id);

	// 이름과 전화번호로 User 찾기
	Optional<User> findByNameAndPhoneNumber(String name, String phoneNumber);

	// id와 이름과 전화번호로 User 찾기
	Optional<User> findByIdAndNameAndPhoneNumber(String id, String name, String phoneNumber);
	
	// 관리자가 모든 유저 찾기
	@Query("select u from User u where u.role = 'ROLE_User' or u.role = 'ROLE_EMPLOYEE'")
	List<User> findUserAll();
	
	// id와 권한으로 User 찾기
	Optional<User> findByIdAndRole(String id, String role);
}
