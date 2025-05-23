package com.example.rbs.controller;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.rbs.service.ServerStatusService;

@RestController
public class ServerStatusController {
	
	private ServerStatusService serverStatusService;
	
	public ServerStatusController(ServerStatusService serverStatusService) {
		this.serverStatusService = serverStatusService;
	}
	
	// 서버 상태 확인
	// DB, 앱 백 서버, 파이썬 서버
	@GetMapping("admin/serverStatus")
	public Map<String, String> serverStatus() {
		return serverStatusService.serverStatus();
	}
	
}
