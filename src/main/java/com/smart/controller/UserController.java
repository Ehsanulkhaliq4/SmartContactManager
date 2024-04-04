package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping(value = "/user", method = RequestMethod.GET)
public class UserController {
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ContactRepository contactRepository;

	// Method For Add Common Data For Response

	@ModelAttribute
	public void addCommonData(Model m, Principal principal) {

		String name = principal.getName();
		System.out.println("USERNAME " + name);

		User user = userRepository.getUserByUserName(name);
		System.out.println("USER\n" + user);

		m.addAttribute("user", user);

	}

	// dashboard Home
	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String dashboard(Model model, Principal principal) {

		model.addAttribute("title", "User Dashboard");
		return "normal/user_dashboard";
	}

	// Open Add Form Controller
	@GetMapping("/add-contact")
	public String openAddContactForm(Model model) {
		model.addAttribute("title", "Add Contact");
		model.addAttribute("Contact", new Contact());
		return "normal/add_contact_form";
	}

	// Processing add contact form
	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
			Principal principal, HttpSession session) {
		try {
			String name = principal.getName();
			User user = this.userRepository.getUserByUserName(name);
			// Processing and uploading File
			if (file.isEmpty()) {
				// if the file is empty Try our message
				System.out.println("Image is Empty");
				contact.setImage("contact.png");
			} else {
				// upload the file to folder and update the name to contact
				contact.setImage(file.getOriginalFilename());
				File saveFile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				System.out.println("Image Uploaded Successfully");
			}

			contact.setUser(user);
			user.getContacts().add(contact);
			this.userRepository.save(user);
			System.out.println("DATA " + contact);
			System.out.println("Added To Database");
			// Message Success
			session.setAttribute("message", new Message("Your Contact is Added!! Add More", "success"));
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("ERROR " + e.getMessage());
			e.printStackTrace();
			// Message Error
			session.setAttribute("message", new Message("Something Wents Wrong!! Try Again", "danger"));
		}
		return "normal/add_contact_form";

	}

	// show contacts Handler
	// Per page 5 contacts dekahana hn hum is barha bhi sakta hn
	// Current page 0 ha or hum is varable [page] ma store krein ga
	@GetMapping("show-contacts/{page}")
	public String showContact(@PathVariable("page") Integer page, Model m, Principal principal) {
		m.addAttribute("title", "Show User Contacts");
		// Contacts ki list bhejini ha
		// Ik ya tareeka
		String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);

		// Is ka pass 2 information hn
		// Current page
		// contact per page 5

		Pageable pageable = PageRequest.of(page, 5);
		Page<Contact> contacts = this.contactRepository.findContactByUser(user.getId(), pageable);
		m.addAttribute("contacts", contacts);
		m.addAttribute("currentPage", page);
		m.addAttribute("totalPages", contacts.getTotalPages());
		return "normal/show_Contacts";
	}

	// Showing particular contacts details

	@GetMapping("/{cId}/contact")
	public String showContactDetail(@PathVariable("cId") Integer cId, Model model, Principal principal) {
		System.out.println("CID " + cId);
		Optional<Contact> contactOptional = this.contactRepository.findById(cId);
		Contact contact = contactOptional.get();

		// For fixing Bugs
		String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);
		if (user.getId() == contact.getUser().getId()) {
			model.addAttribute("contact", contact);
			model.addAttribute("title", contact.getName());
		}
		return "normal/contact_detail";

	}

	// delete handler controller
	@GetMapping("/delete/{cid}")
	public String deleteContact(@PathVariable("cid") Integer cId, Model model, HttpSession session,Principal principal) {
		Contact contact = this.contactRepository.findById(cId).get();
		// check lagana ha.... ya asignment ha
		// 2sri assignment image ko bhi delete krna ha
		// photo ho gi img folder or mila gi humeun .getimage sa
		System.out.println("Contact  " + contact.getcId());
		/*
		 * contact.setUser(null); this.contactRepository.deleteByIdCustom(cId);
		 */
		//Conatact ko delete krna ka dosra tareeka
		  User user = this.userRepository.getUserByUserName(principal.getName());
		 user.getContacts().remove(contact);
		 this.userRepository.save(user);
		session.setAttribute("message", new Message("Your Contact Deleted SuccessFully....", "success"));
		
		
		 
		return "redirect:/user/show-contacts/0";
		
	}

	//open update form handler
	@PostMapping("/update-contact/{cid}")
	public String updateForm(@PathVariable("cid") Integer cId,Model model) {
		
		model.addAttribute("title","Update-Form Smart Contact Manager");
		Contact contact = this.contactRepository.findById(cId).get();
		model.addAttribute("contact",contact);
		return "normal/update_form";
	}
	//Update Contact Handler
	@RequestMapping(value = "/process-update",method=RequestMethod.POST)
	public String updateHandler(@ModelAttribute Contact contact,@RequestParam("profileImage") MultipartFile file,Model m,HttpSession session,Principal principal) 
	{
		try {
			//Old contact ki detail oehla nikalni ha 
			Contact oldContactDetail = this.contactRepository.findById(contact.getcId()).get();
			
			
			//First check image
			if(!file.isEmpty()) {
				//File empty nhi ha is liya hamein rewrite krni ha
				
				
				//Pehla old photo delete krni ha 
				File deleteFile = new ClassPathResource("static/img").getFile();
				File file1=new File(deleteFile,oldContactDetail.getImage());
				file1.delete();
				
				//Phir Nai Upload Krni ha
				File saveFile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				contact.setImage(file.getOriginalFilename());
			}else
			{
				contact.setImage(oldContactDetail.getImage());
			}
			User user = this.userRepository.getUserByUserName(principal.getName());
			contact.setUser(user);
			this.contactRepository.save(contact);
			session.setAttribute("message", new Message("Your Contact Is Updated SuccessFully...", "success"));
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		System.out.println("CONTACT NAME "+contact.getName());
		System.out.println("CONTACT ID "+contact.getcId());
		return"redirect:/user/"+contact.getcId()+"/contact";
	}
	
	//Your profile Handler
	@GetMapping("/profile")
	public String profile(Model model)
	{
		model.addAttribute("title" ,"Your Profile Page");
		return"normal/profile";
	}
	//Open Setting handler
	@GetMapping("/settings")
	public String openSetting(Model model) {
		model.addAttribute("title","Setting-Page");
		return "normal/settings";
	}
	//Change password method/handler...
	@PostMapping("/change-password")
	public String ChangePassword(@RequestParam("oldPassword") String oldPassword,@RequestParam("newPassword") String newPassword,Principal principal,HttpSession session) 
	{
		System.out.println("OLD PASSWORD  "+oldPassword);
		System.out.println("NEW PASSWORD  "+newPassword);
		String userName = principal.getName();
		User currentUser = this.userRepository.getUserByUserName(userName);
		System.out.println(currentUser.getPassword());
		if(this.bCryptPasswordEncoder.matches(oldPassword, currentUser.getPassword())) {
			//Change the password
			currentUser.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
			this.userRepository.save(currentUser);
			session.setAttribute("message", new Message("Your Password is SuccessFully Changed...","success"));
		}else {
			//old password not matches give error
			session.setAttribute("message", new Message("Please Enter Correct Old Password!!!","danger"));
			return "redirect:/user/settings";
		}
		
		return "redirect:/user/index";
		
	}
	//Creating order for payment
	@PostMapping("/create_order")
	@ResponseBody
	public String createOrder(@RequestBody Map<String, Object> data) 
	{
		System.out.println(data);
		System.out.println("Hey order Funtion executed");
		int amt=Integer.parseInt(data.get("amount").toString());
		return "done";
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
}
