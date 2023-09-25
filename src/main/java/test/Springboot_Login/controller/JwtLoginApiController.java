package test.Springboot_Login.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import test.Springboot_Login.auth.JwtTokenUtil;
import test.Springboot_Login.domain.User;
import test.Springboot_Login.dto.JoinRequest;
import test.Springboot_Login.dto.LoginRequest;
import test.Springboot_Login.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/jwt-login")
public class JwtLoginApiController {

    private final UserService userService;

    @PostMapping("/join")
    public String join(@RequestBody JoinRequest joinRequest) {

        // loginId 중복 체크
        if(userService.checkLoginIdDuplicate(joinRequest.getLoginId())) {
            return "로그인 아이디가 중복됩니다.";
        }
        // 닉네임 중복 체크
        if(userService.checkNickNameDuplicate(joinRequest.getNickName())) {
            return "닉네임이 중복됩니다.";
        }
        // password와 passwordCheck가 같은지 체크
        if(!joinRequest.getPassword().equals(joinRequest.getPasswordCheck())) {
            return"바밀번호가 일치하지 않습니다.";
        }

        userService.join2(joinRequest);
        return "회원가입 성공";
    }
    
    @PostMapping("/login")
    public String login(@RequestBody LoginRequest loginRequest) {
        User user= userService.login(loginRequest);
        
        if(user == null) {
            return "로그인 아이디 또는 비밀번호가 틀렸습니다.";
        }
        
        //로그인 성공 시, jwt token 발급
        String secretKey = "my-secret-key-1234";
        long expireTimeMs = 1000 * 60 * 60; //token 유효시간 = 60분
        
        String jwtToken = JwtTokenUtil.createToken(user.getLoginId(), secretKey, expireTimeMs);
        
        return jwtToken;
    }

    @GetMapping("/info")
    public String userInfo(Authentication authentication) {
        User loginUser = userService.getLoginUserByLoginId(authentication.getName());

        return String.format("loginId : %s\nnickName : %s\nrole : %s",
                loginUser.getLoginId(), loginUser.getNickName(), loginUser.getRole().name());
    }

    @GetMapping("/admin")
    public String adminPage() {

        return "관리자 페이지 접근 성공";
    }

}
