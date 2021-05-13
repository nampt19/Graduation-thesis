package com.example.nampt.service.ServiceImpl;

import com.example.nampt.entity.User;
import com.example.nampt.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class BaseService {

    @Autowired
    private  UserRepository userRepository;

    public boolean checkToken(String token) {
        User user = userRepository.findByAccessToken(token);
        return user != null;
    }
}
