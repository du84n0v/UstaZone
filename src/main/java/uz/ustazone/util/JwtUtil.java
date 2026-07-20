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
    private static final int accessTokenLiveTime = 1000 * 3600*24; // 1-hour
    private static final long refreshTokenLiveTime = 1000L * 3600 * 24 * 30; // 1-month
    private static String secretKey;

    @Value("${jwt.secret.key}")
    public void setSecretKey(String key) {
        JwtUtil.secretKey = key;
    }

    public static String getToken(JwtDTO jwt, Boolean wh) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("id", jwt.getId());
        extraClaims.put("role", jwt.getRole());

        return Jwts
                .builder()
                .claims(extraClaims)
                .subject(jwt.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + (wh ? accessTokenLiveTime : refreshTokenLiveTime))) // 1-soatlik umr
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

        // Bazida ID Integer yoki Long bo'lib kelsa, xato bermasligi uchun toString() qilgan ma'qul
        String id = claims.get("id").toString();
        String role = (String) claims.get("role");
        return new JwtDTO(id, username, role);
    }

    private static SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
