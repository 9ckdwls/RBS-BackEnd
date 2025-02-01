package com.example.rbs.service;

import org.springframework.stereotype.Service;
import com.example.rbs.repository.ItemRepository;

@Service
public class ItemService {
	
	private final ItemRepository itemRepository;
	
	public ItemService(ItemRepository itemRepository) {
		this.itemRepository = itemRepository;
	}
	
}
