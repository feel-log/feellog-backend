package com.feellog.backend.domain.master.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CategoryGroupDto {
	private Long id;
	private String name;
	private List<CategoryDto> categories;
}
