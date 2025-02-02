package com.example.rbs.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
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
}
