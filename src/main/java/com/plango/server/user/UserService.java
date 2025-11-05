package com.plango.server.user;

import com.plango.server.exception.DataNotFoundException;
import com.plango.server.user.dto.UserCreateRequest;
import com.plango.server.user.dto.UserReadResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;


// 리포지터리에서 반환한 값이 optional
// 잘 처리해서 리턴
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //DB 내부 키 찾는 함수
    @Transactional(readOnly = true)
    public Long getUserIdByPublicId(String publicId) {
        Optional<UserEntity> userEntity = userRepository.findByPublicId(publicId);
        if (userEntity.isPresent()) {
            return userEntity.get().getId();
        }
        else  {
            throw new DataNotFoundException("User not found");
        }
    }

    //Create
    @Transactional
    public String createUser(UserCreateRequest userCreateRequest) {
        String nickname = userCreateRequest.nickname();
        String mbti = userCreateRequest.mbti();

        String publicId = UUID.randomUUID().toString(); //공개 키 생성

        UserEntity userEntity = new UserEntity(publicId, nickname,mbti);
        userRepository.save(userEntity);
        userRepository.flush();

        return publicId;
    }

    // Read
    @Transactional(readOnly = true)
    public UserReadResponse getUserByPublicId(String publicId){
        Optional<UserEntity> userEntity = userRepository.findByPublicId(publicId);
        if(userEntity.isPresent()){
            UserEntity ue1 = userEntity.get();
            return new UserReadResponse(ue1.getPublicId(),ue1.getNickname(),ue1.getMbti());
        }
        else throw new DataNotFoundException("User not found");
    }

    //NOTE AI 테스트
    @Transactional(readOnly = true)
    public String getUserNicknameByPublicId(String publicId){
        Optional<UserEntity> userEntity = userRepository.findByPublicId(publicId);
        if(userEntity.isPresent()){
            return userEntity.get().getNickname();
        }
        else throw new DataNotFoundException("User not found");
    }
}
