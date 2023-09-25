package test.Springboot_Login.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import test.Springboot_Login.domain.User;
import test.Springboot_Login.service.UserService;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final UserService userService;
    private final String secretKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        //Header의 authorization 값이 비어있다면, jwt token을 전송하지 않음 -> 로그인 x
        if(authorizationHeader == null) {
            filterChain.doFilter(request, response);

            return;
        }

        //Header의 authorization 값이 'Bearer'로 시작하지 않는다면 -> 잘못된 토큰이다.
        if(!authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);

            return;
        }

        //전송 받은 값에서 'Bearer '의 뒷부분 (jwt token) 추출
        String token = authorizationHeader.split(" ")[1];

        //전송받은 jwt token이 만료되었으면 -> 다음 필터 진행(인증 x)
        if(JwtTokenUtil.isExpired(token, secretKey)) {
            filterChain.doFilter(request, response);

            return;
        }

        //jwt token에서 loginId 추출
        String loginId = JwtTokenUtil.getLoginId(token, secretKey);

        //추출한 loginId에서 유저 찾아오기
        User loginUser = userService.getLoginUserByLoginId(loginId);

        //loginUser 정보로 UsernamePasswordAuthentication Token 발급
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginUser.getLoginId(), null, List.of(new SimpleGrantedAuthority(loginUser.getRole().name()))
        );
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        //권한 부여
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request, response);
    }
}
