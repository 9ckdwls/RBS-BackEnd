package com.example.rbs.service;

import org.springframework.stereotype.Service;
import com.example.rbs.repository.BoxRepository;

@Service
public class BoxService {

	private final BoxRepository repository;
	public BoxService(BoxRepository repository) {
		this.repository = repository;
	}

}
