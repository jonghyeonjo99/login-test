package test.Springboot_Login.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class JwtTokenUtil {

    public static String createToken(String loginId, String key, long expireTimeMs) {

        //claim = jwt token에 들어갈 정보로, loginId를 넣어준다.
        Claims claims = Jwts.claims();
        claims.put("loginId", loginId);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expireTimeMs))
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();
    }

    //claims에서 loginId 꺼내기
    public static String getLoginId(String token, String secretKey) {
        return extractClaims(token, secretKey).get("loginId").toString();
    }

    //발급된 token의 만료시간이 지났는지 확인
    public static boolean isExpired(String token, String secretKey) {
        Date expiredDate = extractClaims((token, secretKey).getExpiration());
        
        return expiredDate.before(new Date());
    }
    
    //secretKey를 사용해 token 파싱
    private static Claims extractClaims(String token, String secretKey) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }
}
