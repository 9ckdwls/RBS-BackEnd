package com.example.rbs.controller;

import org.springframework.web.bind.annotation.RestController;
import com.example.rbs.service.CodeService;

@RestController
public class CodeController {
	
	private final CodeService codeService;
	
	public CodeController(CodeService codeService) {
		this.codeService = codeService;
	}

}
