package com.example.repository;

import java.lang.StackWalker.Option;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.entity.CategoryEntity;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
	//select * from tbl_categories where profile_id=?
	List<CategoryEntity> findByProfileId(Long profileId);
	
	//select * from tbl_categories where id=? and profile_id=?
	Optional<CategoryEntity> findByIdAndProfileId(Long id,Long profileId);
	
	//select * from tbl_categories where type=? and profile_id=?
	List<CategoryEntity> findByTypeAndProfileId(String type,Long profileId);
	
	Boolean existsByNameAndProfileId(String name,Long profileId);

}
