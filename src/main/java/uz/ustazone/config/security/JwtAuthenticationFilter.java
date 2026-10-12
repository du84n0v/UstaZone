package uz.ustazone.config.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import uz.ustazone.dto.security.JwtDTO;
import uz.ustazone.util.JwtUtil;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    // shouldNotFilter metodi olib tashlandi, endi filtr barcha so'rovlar uchun ishlaydi,
    // lekin token yo'q bo'lsa shunchaki o'tkazib yuboradi (ruxsatlarni Spring Security hal qiladi).

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        final String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = header.substring(7).trim();

        try {
            JwtDTO jwtDTO = JwtUtil.decode(token);
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(jwtDTO.getUsername());

            // Bloklangan yoki nofaol foydalanuvchini tekshiramiz
            if (!userDetails.isAccountNonLocked() || !userDetails.isEnabled()) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Profilingiz bloklangan yoki faol emas!\"}");
                return;
            }

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (JwtException | UsernameNotFoundException e) {
            // Token xato bo'lsa yoki muddati tugagan bo'lsa ham o'tkazib yuboramiz,
            // ruxsat talab qilingan joyda 401 qilib qaytaradi
        }

        filterChain.doFilter(request, response);
    }
}