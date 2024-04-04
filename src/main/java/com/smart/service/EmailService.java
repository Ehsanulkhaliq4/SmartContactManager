package com.smart.service;

import org.springframework.stereotype.Service;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

@Service
public class EmailService {
	
	  public boolean sendEmail(String to ,String from ,String subject,String text){

	        boolean flag=false;
	          //Logic
	        //Step 1 : smpt properties setting
	        Properties properties = new Properties();
	        properties.put("mail.smtp.auth",true);
	        properties.put("mail.smtp.starttls.enable",true);
	        properties.put("mail.smtp.port","587");
	        properties.put("mail.smtp.host","smtp.gmail.com");

	        String username="virtualsociety274";
	        String password="jjkmeyhrbrsbkjhv";
	        //Session get krna ha
	        Session session = Session.getInstance(properties, new Authenticator() {
	            @Override
	            protected PasswordAuthentication getPasswordAuthentication() {
	                return new PasswordAuthentication(username, password);
	            }
	        });

	        try {

	            Message message=new MimeMessage(session);
	            message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
	            message.setFrom(new InternetAddress(from));
	            message.setSubject(subject);
	            message.setText(text);
//	            message.setContent(message,"text/html");
	            Transport.send(message);
	            flag=true;
	        }
	        catch (Exception e){
	            e.printStackTrace();
	        }

	        return flag;
	    }

}
