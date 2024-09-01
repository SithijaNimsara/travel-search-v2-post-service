package com.example.postservice.service;



import com.example.postservice.dto.UserInforDto;
import com.example.postservice.entity.User;
import com.example.postservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));
        UserInforDto userInforDto = UserInforDto.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .password(user.getPassword())
                .email(user.getEmail())
                .address(user.getAddress())
                .state(user.getState())
                .country(user.getCountry())
                .image(user.getImage())
                .role(user.getRole())
                .build();
        return userInforDto;
    }
}
