package com.example.rbs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.rbs.entity.User;


public interface UserRepository extends JpaRepository<User, String>{
}
