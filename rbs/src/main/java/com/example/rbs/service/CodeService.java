package com.example.rbs.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.rbs.entity.Code;
import com.example.rbs.repository.CodeRepository;

@Service
public class CodeService {
	
	private final CodeRepository codeRepository;

	public CodeService(CodeRepository codeRepository) {
		this.codeRepository = codeRepository;
	}

	// 수거자 가입 코드 찾기
	public int findCode() {
		Optional<Code> code = codeRepository.findCode();
		if(code.isPresent()) {
			return code.get().getCode();
		}
		return -1;
	}

	// 수거자 가입 코드 변경
	public int updateCode(int newCode) {
		Optional<Code> code = codeRepository.findCode();
		if(code.isPresent()) {
			codeRepository.delete(code.get());
			Code myCode = new Code();
			myCode.setCode(newCode);
			codeRepository.save(myCode);
			return 1;
		}
		return 0;
	}
	
	

}
