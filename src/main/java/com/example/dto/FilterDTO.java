package com.example.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FilterDTO {

	private String type;
	
	private LocalDate startDate;
	private LocalDate endDate;
	private String keyword;
	
	private String sortField;// date,amount, name
	private String sortOrder;// asc 0r desc
}
