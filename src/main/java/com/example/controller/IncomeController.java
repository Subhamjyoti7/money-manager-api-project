package com.example.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.dto.ExpenseDTO;
import com.example.dto.IncomeDTO;
import com.example.service.IncomeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1.0/incomes")
@RequiredArgsConstructor
public class IncomeController {
	
	private final IncomeService incomeService;
	
	@PostMapping
	public ResponseEntity<IncomeDTO> addIncome(@RequestBody IncomeDTO dto){
		IncomeDTO saved= incomeService.addIncome(dto);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(saved);
	}
	
	@GetMapping
	public ResponseEntity<List<IncomeDTO>> getIncome(){
		List<IncomeDTO> expense= incomeService.getCurrentMonthIncomesForCurrentUser();
		
		return ResponseEntity.ok(expense);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteIncome(
			@PathVariable Long id){
		incomeService.deleteIncome(id);
		return ResponseEntity.noContent().build();
	}

}
