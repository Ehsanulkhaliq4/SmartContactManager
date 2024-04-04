package com.smart.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.smart.dao.UserRepository;
import com.smart.entities.User;

public class UserDetailServiceImpl implements UserDetailsService{
    
	@Autowired
	private UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		//Fething user from database
	//Hamein database sa user ko lana ha or userdetail ko dena ha ya class is liya banata hn 
		//is ka liya zarori ha ka hm userRepository ko yahan Autowire krein ta ka hm us ka method istamal kr sakein 
		User user = userRepository.getUserByUserName(username);
		if(user==null) {
			
		}
		CustumUserDetail custumUserDetail=new CustumUserDetail(user);
		return custumUserDetail;
	}

}
