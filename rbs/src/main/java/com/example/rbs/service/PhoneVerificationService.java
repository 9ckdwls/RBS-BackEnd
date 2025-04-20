package com.example.rbs.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class PhoneVerificationService {

    private final Map<String, String> verificationCodes = new ConcurrentHashMap<>();
    private final Map<String, Long> verificationTimestamps = new ConcurrentHashMap<>();

    // 인증 코드 유효 시간 (예: 5분)
    private static final long CODE_EXPIRATION_TIME = 5 * 60 * 1000;

    // 인증 코드 생성 및 저장
    public String generateVerificationCode(String phone) {
        String code = String.valueOf((int) (Math.random() * 900000) + 100000); // 6자리 랜덤 숫자
        verificationCodes.put(phone, code);
        verificationTimestamps.put(phone, System.currentTimeMillis());
        return code;
    }

    // 인증 코드 확인 (유효 시간 체크)
    public String verifyCode(String phone, String code) {
        String storedCode = verificationCodes.get(phone);
        Long timestamp = verificationTimestamps.get(phone);

        if (storedCode == null || timestamp == null) {
            return "Fail"; // 인증 코드 미존재
        }

        // 인증 코드 만료 시간 확인
        if (System.currentTimeMillis() - timestamp > CODE_EXPIRATION_TIME) {
            verificationCodes.remove(phone);
            verificationTimestamps.remove(phone);
            return "Fail"; // 인증 코드 만료
        }
        
        if(storedCode.equals(code)) {
        	return "Success";
        } else {
        	return "Fail";
        }
    }
}
