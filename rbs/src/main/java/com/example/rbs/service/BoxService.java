package com.example.rbs.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.rbs.entity.Box;
import com.example.rbs.repository.BoxRepository;

@Service
public class BoxService {

	private final BoxRepository repository;
	public BoxService(BoxRepository repository) {
		this.repository = repository;
	}
	
	// 모든 수거함 조회
	public List<Box> findAllBox() {
		return repository.findAll();
	}

	// 수거함 이름으로 검색
	public Box findBoxByName(String name) {
		Optional<Box> box = repository.findByBoxId_name(name);
		if(box.isPresent()) {
			return box.get();
		} else {
			return null;
		}
	}

}
