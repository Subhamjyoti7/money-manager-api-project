package com.example.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.dto.AuthDTO;
import com.example.dto.ProfileDTO;
import com.example.service.ProfileService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1.0")
public class ProfileController {

    private final PasswordEncoder passwordEncoder;
	
	private final ProfileService profileService;


    
	

	@PostMapping("/register")
	public ResponseEntity<ProfileDTO> registerProfile(@RequestBody ProfileDTO profileDTO){
		
		ProfileDTO registerProfile=profileService.registerProfile(profileDTO);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(registerProfile);
	}
	
	@GetMapping("/activate")
	public ResponseEntity<String> activateProfile(
			 
		@RequestParam String token){
		boolean isActivated= profileService.activateProfile(token);
		
		if(isActivated) {
			return ResponseEntity.ok("Profile activated successfully");
			
		}else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Activation token not found or already used");
		}
	}
	
	@PostMapping("/login")
	public ResponseEntity<Map<String, Object>> login(@RequestBody AuthDTO authDTO){
		
		try {
			
			if(!profileService.isAccountActive(authDTO.getEmail())) {

				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message","Account is not active ,please activate your accountfirst"));
			}
			
			Map<String, Object> response= profileService.authenticateAndGenerateToken(authDTO);
			return  ResponseEntity.ok(response);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message",e.getMessage()));
		}
	}
	
	@GetMapping("/test")
	public String test() {
		return "Test Successfully";
	}
	
	@GetMapping("/profile")
	public ResponseEntity<ProfileDTO> getPublicProfile(){
		ProfileDTO profileDTO= profileService.getPublicProfile(null);
		return ResponseEntity.ok(profileDTO);
	}
	
	
	
}
