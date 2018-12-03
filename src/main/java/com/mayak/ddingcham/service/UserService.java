package com.mayak.ddingcham.service;

import com.mayak.ddingcham.domain.User;
import com.mayak.ddingcham.domain.UserRepository;
import com.mayak.ddingcham.dto.UserInputDTO;
import com.mayak.ddingcham.exception.UnAuthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

@Slf4j
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User create(UserInputDTO user) {
        log.debug("create : {}", user);
        return userRepository.save(user.toEntity());
    }

    public User login(User user){
        log.debug("login : {}", user);
        return userRepository.findByUuid(user.getUuid()).orElseThrow(() -> new UnAuthorizedException("로그인이 필요합니다"));
    }

    public User logout(User user) {
        return userRepository.findByUuid(user.getUuid()).orElseThrow(() -> new EntityNotFoundException());
    }
}
