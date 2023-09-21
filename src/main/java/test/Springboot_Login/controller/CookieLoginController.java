package test.Springboot_Login.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import test.Springboot_Login.domain.User;
import test.Springboot_Login.domain.UserRole;
import test.Springboot_Login.dto.JoinRequest;
import test.Springboot_Login.dto.LoginRequest;
import test.Springboot_Login.service.UserService;


@Controller
@RequiredArgsConstructor
@RequestMapping("/cookie-login")
public class CookieLoginController {

    private final UserService userService;

    @GetMapping(value = {"", "/"})
    public String home(@CookieValue(name = "userId", required = false)Long userId, Model model) {
        model.addAttribute("loginType", "cookie-login");
        model.addAttribute("pageName", "쿠키 로그인");

        User loginUser = userService.getLoginUserById(userId);

        if(loginUser != null) {
            model.addAttribute("nickName",loginUser.getNickName());
        }

        return "home";
    }

    @GetMapping("/join")
    public String joinPage(Model model) {
        model.addAttribute("loginType", "cookie-login");
        model.addAttribute("pageName", "쿠키 로그인");

        model.addAttribute("joinRequest", new JoinRequest());

        return "join";
    }

    @PostMapping("/join")
    public String join(@Valid @ModelAttribute JoinRequest joinRequest, BindingResult bindingResult, Model model) {
        model.addAttribute("loginType", "cookie-login");
        model.addAttribute("pageName", "쿠키 로그인");

        //아이디 중복 체크
        if(userService.checkLoginIdDuplicate(joinRequest.getLoginId())) {
            bindingResult.addError(new FieldError("joinRequest", "loginId", "로그인 아이디가 중복됩니다."));
        }

        //닉네임 중복 체크
        if(userService.checkNickNameDuplicate(joinRequest.getNickName())) {
            bindingResult.addError(new FieldError("joinRequest", "nickName","닉네임이 중복됩니다."));
        }

        //password와 passwordcheck이 같은지 확인
        if(!joinRequest.getPassword().equals(joinRequest.getPasswordCheck())) {
            bindingResult.addError(new FieldError("joinRequest", "passwordCheck", "비밀번호가 일치하지 않습니다."));
        }

        if(bindingResult.hasErrors()) {
            return "join";
        }

        userService.join(joinRequest);
        return "redirect:/cookie-login";
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("loginType", "cookie-login");
        model.addAttribute("pageName", "쿠키 로그인");

        model.addAttribute("loginRequest", new LoginRequest());

        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute LoginRequest loginRequest, BindingResult bindingResult,
                        HttpServletResponse response, Model model) {
        model.addAttribute("loginType", "cookie-login");
        model.addAttribute("pageName", "쿠키 로그인");

        User user = userService.login(loginRequest);

        if(user == null) {
            bindingResult.reject("loginFail", "로그인 아이디 또는 비밀번호가 틀렸습니다.");
        }

        if(bindingResult.hasErrors()) {
            return "login";
        }

        //로그인 성공 시, 쿠키 생성
        Cookie cookie = new Cookie("userId", String.valueOf(user.getId()));
        cookie.setMaxAge(60 * 60);
        response.addCookie(cookie);

        return "redirect:/cookie-login";
    }

    //로그아웃 시, 생성되었던 쿠키 삭제
    @GetMapping("/logout")
    public String logout(HttpServletResponse response, Model model) {
        model.addAttribute("loginType", "cookie-login");
        model.addAttribute("pageName", "쿠키 로그인");

        Cookie cookie = new Cookie("userId", null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return "redirect:/cookie-login";
    }

    @GetMapping("/info")
    public String userInfo(@CookieValue(name = "userId", required = false)Long userId, Model model) {
        model.addAttribute("loginType", "cookie-login");
        model.addAttribute("pageName", "쿠키 로그인");

        User loginUser = userService.getLoginUserById(userId);

        if(loginUser == null) {
            return "redirect:/cookie-login/login";
        }

        model.addAttribute("user",loginUser);

        return "info";
    }

    @GetMapping("/admin")
    public String adminPage(@CookieValue(name = "userId", required = false)Long userId, Model model) {
        model.addAttribute("loginType", "cookie-login");
        model.addAttribute("pageName", "쿠키 로그인");

        User loginUser = userService.getLoginUserById(userId);

        if(loginUser == null) {
            return "redirect:/cookie-login/login";
        }

        if(!loginUser.getRole().equals(UserRole.ADMIN)) {
            return "redirect:/cookie-login";
        }

        return "admin";
    }
}
