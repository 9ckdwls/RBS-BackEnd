package com.example.rbs.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.rbs.entity.Item;
import com.example.rbs.repository.ItemRepository;

@Service
public class ItemService {

	private final ItemRepository itemRepository;

	public ItemService(ItemRepository itemRepository) {
		this.itemRepository = itemRepository;
	}

	// 상품 전부 가져오기
	public List<Item> getItems() {
		return itemRepository.findAll();
	}

}
