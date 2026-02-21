package com.example.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.dto.CategoryDTO;
import com.example.entity.CategoryEntity;
import com.example.entity.ProfileEntity;
import com.example.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {
	
	private final ProfileService profileService;
	private final  CategoryRepository categoryRepository;
	
	//save category
	public CategoryDTO saveCategory(CategoryDTO categoryDTO) {
		ProfileEntity profile=profileService.getCurrentProfile();
		if(categoryRepository.existsByNameAndProfileId(categoryDTO.getName(), profile.getId())) {
			throw new RuntimeException("Category with this name already exist");
		}
		
		CategoryEntity newCategory=toEntity(categoryDTO, profile);
		newCategory= categoryRepository.save(newCategory);
		return toDTO(newCategory);
	}
	
	//get categories for current user
	
	public List<CategoryDTO>  getCategoriesForCurrentUser(){
		ProfileEntity profile= profileService.getCurrentProfile();
		List<CategoryEntity> categories= categoryRepository.findByProfileId(profile.getId());
		
		return categories.stream().map(this::toDTO).toList();
		
	}
	
	//get categories by type for current user 
	
    public List<CategoryDTO>  getCategoriesByTypeForCurrentUser(String type){
    	ProfileEntity profile= profileService.getCurrentProfile();
    	List<CategoryEntity> entities = categoryRepository.findByTypeAndProfileId(type, profile.getId());
    	
    	return entities.stream().map(this::toDTO).toList();
    	
    }
    
    public CategoryDTO updateCategory(Long categoryId,CategoryDTO dto) {
    	
    	ProfileEntity profile= profileService.getCurrentProfile();
    	CategoryEntity existingCategoty =categoryRepository.findByIdAndProfileId(categoryId, profile.getId())
    	.orElseThrow(()-> new RuntimeException("Category not found or not accessible"));
    	
    	existingCategoty.setName(dto.getName());
    	existingCategoty.setIcon(dto.getIcon());
    	existingCategoty.setType(dto.getType());
    	
    	existingCategoty=categoryRepository.save(existingCategoty);
    	return toDTO(existingCategoty);
    	
    	
    	
    	
    }
    
	
	
	//helper methods 
	private CategoryEntity toEntity(CategoryDTO categoryDTO,ProfileEntity profile) {
		return CategoryEntity.builder()
				.name(categoryDTO.getName())
				.icon(categoryDTO.getIcon())
				.profile(profile)
				.type(categoryDTO.getType())
				.build();
	}
	
	private CategoryDTO toDTO(CategoryEntity entity) {
		return CategoryDTO.builder()
				.id(entity.getId())
				.profileId(entity.getProfile()!= null ? entity.getProfile().getId():null)
				.name(entity.getName())
				.icon(entity.getIcon())
				.updatedAt(entity.getUpdatedAt())
				.createdAt(entity.getCreatedAt())
				.type(entity.getType())
				.build();
				
	}

}
