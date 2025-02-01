package com.example.rbs.service;

import org.springframework.stereotype.Service;
import com.example.rbs.repository.CodeRepository;

@Service
public class CodeService {
	
	private final CodeRepository codeRepository;

	public CodeService(CodeRepository codeRepository) {
		this.codeRepository = codeRepository;
	}

}
