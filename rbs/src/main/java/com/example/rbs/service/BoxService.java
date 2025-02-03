package com.example.rbs.service;

import org.springframework.stereotype.Service;
import com.example.rbs.repository.BoxRepository;

@Service
public class BoxService {

	private final BoxRepository boxRepository;
	public BoxService(BoxRepository boxRepository) {
		this.boxRepository = boxRepository;
	}

}
