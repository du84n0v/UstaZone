package uz.ustazone.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uz.ustazone.dto.security.JwtDTO;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {
    private static final int tokenLiveTime = 1000 * 3600 * 24; // 1-day
    private static String secretKey;

    @Value("${jwt.secret.key}")
    public void setSecretKey(String key) {
        JwtUtil.secretKey = key;
    }

    public static String encode(JwtDTO jwt) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("id", jwt.getId());
        extraClaims.put("role", jwt.getRole());

        return Jwts
                .builder()
                .claims(extraClaims)
                .subject(jwt.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + tokenLiveTime))
                .signWith(getSignInKey())
                .compact();
    }

    public static JwtDTO decode(String token) {
        Claims claims = Jwts
                .parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        String username = claims.getSubject();
        Integer id = (Integer) claims.get("id");
        String role = (String) claims.get("role");
        return new JwtDTO(id, username, role);
    }

    private static SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
