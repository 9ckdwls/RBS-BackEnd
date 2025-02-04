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
	// DB, 앱 백 서버, 사용자 앱 프론트 서버, 수거자 앱 프론트 서버 상태
	@GetMapping("admin/serverStatus")
	public Map<String, String> serverStatus() {
		return serverStatusService.serverStatus();
	}
	
	// 프론트에서 접근 X
	// 사용자 앱 프론트 서버 상태 업데이트
	@GetMapping("userFrontServer")
	public void userFrontServer() {
		serverStatusService.updateUserHeartbeat();
	}
	
	// 프론트에서 접근 X
	// 수거자 앱 프론트 서버 상태 업데이트
	@GetMapping("employeeFrontServer")
	public void employeeFrontServer() {
		serverStatusService.updateEmployeeHeartbeat();
	}
	
	
}
