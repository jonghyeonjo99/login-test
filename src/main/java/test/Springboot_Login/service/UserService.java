package test.Springboot_Login.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import test.Springboot_Login.domain.User;
import test.Springboot_Login.dto.JoinRequest;
import test.Springboot_Login.dto.LoginRequest;
import test.Springboot_Login.repository.UserRepository;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    // Spring Security를 사용한 로그인 구현 시 사용
    private final BCryptPasswordEncoder encoder;

    //회원가입 시, 아이디 중복여부 확인
    public boolean checkLoginIdDuplicate(String loginId) {
        return userRepository.existsByLoginId(loginId);
    }

    //회원가입 시, 닉네임 중복여부 확인
    public boolean checkNickNameDuplicate(String nickName) {
        return userRepository.existsByNickName(nickName);
    }
    
    //JoinRequest를 입력받아 User로 변환 후 저장
    //이 과정에서 비밀번호는 암호화되어 저장
    public void join(JoinRequest request) {
        userRepository.save(request.toEntity(encoder.encode(request.getPassword())));
    }

    //로그인 시, 아이디와 비밀번호가 일치하면 User return
    //아이디 혹은 비밀번호가 없거나 다르면 null return
    public User login(LoginRequest request) {
        Optional<User> optionalUser = userRepository.findByLoginId(request.getLoginId());
        
        if(optionalUser.isEmpty()) {
            return null;
        }

        User user = optionalUser.get();

        if(!user.getPassword().equals(request.getPassword())) {
            return null;
        }

        return user;
    }
    
    //userId를 입력받아 user를 return 해주는 기능
    //인증, 인가 시 사용됨
    public User getLoginUserById(Long userId) {
        if(userId == null) return null;
        
        Optional<User> optionalUser = userRepository.findById(userId);

        if(optionalUser.isEmpty()) return null;
        
        return optionalUser.get();
    }

    //loginId를 입력받아 user를 return 해주는 기능
    //인증, 인가 시 사용됨
    public User getLoginUserByLoginId(String loginId) {
        if(loginId == null) return null;

        Optional<User> optionalUser = userRepository.findByLoginId(loginId);

        if(optionalUser.isEmpty()) return null;

        return optionalUser.get();
    }


}
