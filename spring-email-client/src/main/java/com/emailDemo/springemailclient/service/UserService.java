package com.emailDemo.springemailclient.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.emailDemo.springemailclient.dto.UserDto;
import com.emailDemo.springemailclient.entity.User;
import com.emailDemo.springemailclient.repository.UserRepo;

@Service
public class UserService {

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private JavaMailSender mailSender;

	public User createUser(UserDto userDto) {
		User user = new User();
		// Generate verification code
		String verificationCode = UUID.randomUUID().toString();
		user.setVerificationcode(verificationCode);
		user.setVerified(false);
		user.setUsername(userDto.getUsername());
		user.setEmail(userDto.getEmail());
		user.setPassword(userDto.getPassword());
		// Save user to the database
		User userResponse = userRepo.save(user);
		sendVerificationCode(userDto.getEmail(), user.getVerificationcode());
//		sendUrl(userResponse.getEmail());
		return userResponse;
	}

	public void sendVerificationCode(String toemail, String verificationCode) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(toemail);
		message.setSubject("Verification Code");
		message.setText("click this link for verification:    http://localhost:8080/verify/"+toemail +"/"+ verificationCode);
		mailSender.send(message);
		System.out.println(" verification mail send");
	}

//	public void sendUrl(String toemail) {
//		SimpleMailMessage message = new SimpleMailMessage();
//		String url = "http://localhost:8080/verify";
//		message.setTo(toemail);
//		message.setSubject("URL");
//		message.setText("plz click the link: " + url);
//		mailSender.send(message);
//		System.out.println("url mail send");
//	}

	public String verifyUser(String email, String verificationcode) {
		User user = userRepo.findByEmail(email);
//		sendUrl(email);
		if (user != null && user.getVerificationcode().equals(verificationcode)) {
			user.setVerified(true);
			userRepo.save(user);
			return "verified succesfully";
		}
		return "enter valid code";
	}

}
