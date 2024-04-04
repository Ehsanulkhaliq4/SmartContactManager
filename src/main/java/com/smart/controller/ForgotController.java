package com.smart.controller;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.service.EmailService;

import jakarta.servlet.http.HttpSession;

@Controller
public class ForgotController {
	Random random = new Random(1000);
	@Autowired
	private EmailService emailService;
    @Autowired
	private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	//email id form handler
	@RequestMapping("/forgot")
	public String openEmailForm() {
		return "forgot_email_form";
	}
	@PostMapping("/send-otp")
	public String sendOTP(@RequestParam("email")String email,HttpSession session)
	{
		System.out.println("Email "+email);
		//generating Otp of 4 digits
		
		int otp = random.nextInt(999999);
		System.out.println("OTP "+otp);
		
		//write code to send email
		String subject="OTP From SCM";
		String text=""
				+"<div style='border:1px solid #e2e2e2; padding:20px;'>"
				+"<h1>"
				+"OTP is "
				+ "<b>"+otp
				+ "</n>"
				+ "</h1>"
				+"</div>";
		String to=email;
		String from ="virtualsociety@gmail.com";
		 boolean sendEmail = this.emailService.sendEmail(to,from,subject,text);
		 if(sendEmail) {
			 session.setAttribute("myotp",otp);
			 session.setAttribute("email",email);
			 return  "verify_otp";
		 }
		 else {
			 session.setAttribute("message", "Check your email id !!!");
			 return "forgot_email_form";
			 
		 }
	}
	//verify otp
	 @PostMapping("/verify-otp")
	 public String verifyOtp(@RequestParam("otp") int otp,HttpSession session) 
	 {
		 int myOtp=(int)session.getAttribute("myotp");
		 String email=(String)session.getAttribute("email");
		 if(myOtp==otp) {
			 //password change from
			 User user = this.userRepository.getUserByUserName(email);
			 if(user==null)
			 {
			    //send error message
				 session.setAttribute("message", "User does not exits with this email !!!");
				 return "forgot_email_form";
				 
			 }else
			 {
				 //send change password form
				 //Change Password Form dekhana ha 
				 return "passwaord_change_form"; 
			 }
		 }else {
			 session.setAttribute("message", "You have entered wrong otp...");
			 return "verify_otp";
		 }
	 }
	 //Change password
	 @PostMapping("/change-password")
	 public String changePassword(@RequestParam("newpassword") String newpassword,HttpSession session) 
	 {
		 String email=(String)session.getAttribute("email");
		 User user = this.userRepository.getUserByUserName(email);
		 user.setPassword(this.bCryptPasswordEncoder.encode(newpassword));
		 this.userRepository.save(user);
		 return "redirect:/signin?change=password changed successfully...";
	 }
	 
}
