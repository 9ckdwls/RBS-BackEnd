package com.example.rbs.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.rbs.entity.BoxLogItems;
import com.example.rbs.repository.BoxLogItemsRepository;

@Service
public class BoxLogItemsService {

	private final BoxLogItemsRepository boxLogItemsRepository;

	public BoxLogItemsService(BoxLogItemsRepository boxLogItemsRepository) {
		this.boxLogItemsRepository = boxLogItemsRepository;
	}

	// 수거로그 아이디로 아이템 가져오기
	public List<BoxLogItems> getBoxLogItems(int id) {
		List<BoxLogItems> boxLogItems = boxLogItemsRepository.findByBoxLogId(id);
		return boxLogItems;
	}

	public void saveLogItem(String name, int count, int logId) {
		BoxLogItems boxLogItems = new BoxLogItems();
		boxLogItems.setBoxLogId(logId);
		boxLogItems.setName(name);
		boxLogItemsRepository.save(boxLogItems);
	}
}
