package app.auth;

import app.services.UserService;
import app.entities.User;
import app.services.TokenService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

@Component
public class TokenProvider {
    private final UserService userService;
    private final TokenService tokenService;

    public TokenProvider(UserService userService, TokenService tokenService) {
        this.userService = userService;
        this.tokenService = tokenService;
    }

    public String createToken(String username) {
        User user = userService.find(username);
        String token = tokenService.generateNewToken();
        user.setAuthToken(token);
        userService.save(user);

        return token;
    }
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = this.userService.findByAuthToken(token);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String resolveToken(HttpServletRequest req) {
        String tokenHeader = req.getHeader("Authorization");
        if (tokenHeader != null && tokenHeader.startsWith("Bearer ")) {
            return tokenHeader.substring(7);
        }
        return null;
    }
    public boolean validateToken(String token) {
        return userService.findByAuthToken(token) != null;
    }
}
