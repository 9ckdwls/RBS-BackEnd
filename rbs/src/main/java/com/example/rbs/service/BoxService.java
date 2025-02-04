package com.example.rbs.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.rbs.entity.Box;
import com.example.rbs.repository.BoxRepository;

@Service
public class BoxService {

	private final BoxRepository boxRepository;
	public BoxService(BoxRepository boxRepository) {
		this.boxRepository = boxRepository;
	}
	
	// 모든 수거함 조회
	public List<Box> findAllBox() {
		return boxRepository.findAll();
	}

	// 수거함 이름으로 검색
	public Box findBoxByName(String name) {
		Optional<Box> box = boxRepository.findByName(name);
		if(box.isPresent()) {
			return box.get();
		} else {
			return null;
		}
	}

	// 수거함 차단 및 해제
	public String blockBox(int id) {
		Optional<Box> box = boxRepository.findById(id);
		if(box.isPresent()) {
			Box myBox =  box.get();
			if(myBox.getUsed() == -1) { // 누군가 사용 중
				return "-1";
			} else if(myBox.getUsed() == 0) { // 차단하기
				myBox.setUsed(1);
				boxRepository.save(myBox);
				return "차단 성공";
			} else if(myBox.getUsed() == 1) { // 차단 해제하기
				myBox.setUsed(0);
				boxRepository.save(myBox);
				return "차단 해제 성공";
			} else {
				return "Fail";
			}
		} else {
			return "Fail";
		}
	}

	// 사용 중인 수거함 강제 차단
	public String superBlockBox(int id) {
		Optional<Box> box = boxRepository.findById(id);
		if(box.isPresent()) {
			Box myBox =  box.get();
			myBox.setUsed(1);
			boxRepository.save(myBox);
			return "Success";
		} else {
			return "Fail";
		}
	}

}
