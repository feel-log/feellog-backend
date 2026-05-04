package com.feellog.backend.domain.master.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.feellog.backend.domain.master.dto.MasterDataResponseDto;
import com.feellog.backend.domain.master.service.MasterDataService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor

public class MasterDataController {
	private final MasterDataService masterDataService;
	
	@GetMapping("/master-data")
	public MasterDataResponseDto getMasterData() {
		return masterDataService.getMasterData();
	}
}
