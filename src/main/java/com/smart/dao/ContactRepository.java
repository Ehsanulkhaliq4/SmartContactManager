package com.smart.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.entities.Contact;
import com.smart.entities.User;

import jakarta.transaction.Transactional;

public interface ContactRepository extends JpaRepository<Contact, Integer> {

	@Query("from Contact as c where c.user.id=:userId")
	 //Current page
	 //contact per page 5
	
	  
	public Page<Contact> findContactByUser(@Param("userId") int userId,Pageable pageable);
	@Modifying
	@Transactional
	@Query(value="delete from Contact c where c.Id=:id")
	   public void deleteByIdCustom(@Param("id") Integer id);
	//Serarch
	public List<Contact> findByNameContainingAndUser(String name,User user);
} 
