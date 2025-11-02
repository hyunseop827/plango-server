package com.plango.server.user;

import com.plango.server.exception.DataNotFoundException;
import com.plango.server.user.dto.UserCreateRequest;
import com.plango.server.user.dto.UserResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


// 리포지터리에서 반환한 값이 optional
// 잘 처리해서 리턴
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //Create
    @Transactional
    public void createUser(UserCreateRequest userCreateRequest, String id) {
        String name = userCreateRequest.name();
        String mbti = userCreateRequest.mbti();

        UserEntity userEntity = new UserEntity(id,name,mbti);
        userRepository.save(userEntity);
    }

    //Read
    @Transactional(readOnly = true)
    public UserResponse getUserById(String id){
        Optional<UserEntity> userEntity = userRepository.findById(id);
        if(userEntity.isPresent()){
            UserEntity ue1 = userEntity.get();
            UserResponse u1 = new UserResponse(ue1.getName(),ue1.getMbti());
            return u1;
        }
        else throw new DataNotFoundException("User not found");
    }

    //NOTE AI 테스트
    public String getUserNameById(String id){
        Optional<UserEntity> userEntity = userRepository.findById(id);
        if(userEntity.isPresent()){
            UserEntity ue1 = userEntity.get();
            return ue1.getName();
        }
        else throw new DataNotFoundException("User not found");
    }
}
