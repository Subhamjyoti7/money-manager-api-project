package com.example.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.dto.ExpenseDTO;
import com.example.entity.CategoryEntity;
import com.example.entity.ExpenseEntity;
import com.example.entity.ProfileEntity;
import com.example.repository.CategoryRepository;
import com.example.repository.ExpenseRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ProfileService profileService;
	
	private final CategoryRepository categoryRepository;
	private final ExpenseRepository expenseRepository;
	
	


	
	//add a new expense to the database 
	public ExpenseDTO addExpense(ExpenseDTO dto) {
		ProfileEntity profile= profileService.getCurrentProfile();
		CategoryEntity category= categoryRepository.findById(dto.getCategoryId())
				.orElseThrow(()-> new RuntimeException("Category not found"));
		
		 ExpenseEntity newExpense= toEntity(dto, profile, category);
		 newExpense= expenseRepository.save(newExpense);
		 
		 return toDTO(newExpense);
	}
	
	// Retrieves all expenses for current month/based on the start date and end date
	public List<ExpenseDTO> getCurrentMonthExpensesForCurrentUser(){
		
		ProfileEntity profile= profileService.getCurrentProfile();
		
		LocalDate now= LocalDate.now();
		LocalDate startDate=now.withDayOfMonth(1);
		LocalDate enddate=now.withDayOfMonth(now.lengthOfMonth());
		
		List<ExpenseEntity> list= expenseRepository.findByProfileIdAndDateBetween(profile.getId(), startDate, enddate);
		
		return list.stream().map(this::toDTO).toList();
		
	}
	
	//delete expense by id for current user
	public void deleteExpenses(Long expenseId) {
		ProfileEntity profile= profileService.getCurrentProfile();
		ExpenseEntity entity= expenseRepository.findById(expenseId)
		.orElseThrow(()-> new RuntimeException("Expense not found"));
		
		if(!entity.getProfile().getId().equals(profile.getId())) {
			
			throw new RuntimeException("Unauthorized to delete this expenses ");
			
		}
		expenseRepository.delete(entity);
	}
	
	// getting latest 5 expenses for curr user
	public List<ExpenseDTO> getLatest5ExpensesForCurrentUser(){
		
		ProfileEntity profile=profileService.getCurrentProfile();
		
	List<ExpenseEntity> list=	 expenseRepository.findTop5ByProfileIdOrderByDateDesc(profile.getId());
	return list.stream().map(this::toDTO).toList();
	}
	
	
	//get total expenses for current user
	public BigDecimal getTotalExpensesForCurrentUser() {
		ProfileEntity profile=profileService.getCurrentProfile();
	BigDecimal total=expenseRepository.findTotalExpenseByProfileId(profile.getId());
	return total != null? total:BigDecimal.ZERO;
	}
	
	//filter Expenses
	
	public List<ExpenseDTO> filterExpenses(LocalDate startDate,LocalDate endDate,String keyword,Sort sort){
		
		ProfileEntity profile=profileService.getCurrentProfile();
		List<ExpenseEntity> list= 
				expenseRepository.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(profile.getId(), startDate, endDate, keyword, sort);
		return list.stream().map(this::toDTO).toList();
	}
	
	//notifications
	
	public List<ExpenseDTO> getExpensesForUserOnDate(Long profileId,LocalDate date){
		List<ExpenseEntity> list= expenseRepository.findByProfileIdAndDate(profileId, date);
		
		return list.stream().map(this::toDTO).toList();
		
	}
	
	
	
	//helper methods
	
	private ExpenseEntity toEntity(ExpenseDTO dto, ProfileEntity profile,CategoryEntity category) {
		
		return ExpenseEntity.builder()
				.name(dto.getName())
				.icon(dto.getIcon())
				.amount(dto.getAmount())
				.date(dto.getDate())
				.profile(profile)
				.category(category)
				.build();
	}
	
	private ExpenseDTO toDTO(ExpenseEntity entity) {
		return  ExpenseDTO.builder()
				.id(entity.getId())
				.name(entity.getName())
				.icon(entity.getIcon())
				.categoryId(entity.getCategory() != null? entity.getCategory().getId(): null)
				.categoryName(entity.getCategory()!=null? entity.getCategory().getName():null)
				.amount(entity.getAmount())
				.date(entity.getDate())
				.createdAt(entity.getCreatedAt())

				.updatedAt(entity.getUpdatedAt())
				.build();
	}
	

}
