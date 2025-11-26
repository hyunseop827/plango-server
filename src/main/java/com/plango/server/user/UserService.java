package com.plango.server.user;

import com.plango.server.exception.DataNotFoundException;
import com.plango.server.user.dto.UserCreateRequest;
import com.plango.server.user.dto.UserReadResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * user layer service
 */
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    // Create
    @Transactional
    public String createUser(UserCreateRequest userCreateRequest) {
        String nickname = userCreateRequest.nickname();
        String mbti = userCreateRequest.mbti();

        String publicId = UUID.randomUUID().toString(); //공개 키 생성

        UserEntity userEntity = new UserEntity(publicId, nickname,mbti);
        userRepository.saveAndFlush(userEntity);

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
        throw new DataNotFoundException("UserService","해당 유저 없음", "");
    }

    /**
     * AI service url.
     * @param publicId
     * @return
     */
    @Transactional(readOnly = true)
    public String getUserNicknameByPublicId(String publicId){
        Optional<UserEntity> userEntity = userRepository.findByPublicId(publicId);
        if(userEntity.isPresent()){
            return userEntity.get().getNickname();
        }
        throw new DataNotFoundException("UserService","해당 유저 없음", "");
    }

    /**
     * returning user by input public ID
     * @param publicId
     * @return exception or user
     */
    @Transactional(readOnly = true)
    public UserEntity getUserEntityByPublicId(String publicId){
        Optional<UserEntity> userEntity = userRepository.findByPublicId(publicId);
        if(userEntity.isPresent()){
            return userEntity.get();
        }
        throw new DataNotFoundException("UserService","해당 유저 없음", "");
    }

    /**
     * returning user MBTI searching by user public ID
     * @param publicId
     * @return exception or user MBTI
     */
    @Transactional(readOnly = true)
    public String getUserMbtiByPublicId(String publicId){
        Optional<UserEntity> userEntity = userRepository.findByPublicId(publicId);
        if(userEntity.isPresent()){
            return userEntity.get().getMbti();
        }
        throw new DataNotFoundException("UserService","해당 유저 없음", "");
    }
}
