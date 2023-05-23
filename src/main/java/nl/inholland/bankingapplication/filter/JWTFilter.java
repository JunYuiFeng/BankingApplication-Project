package nl.inholland.bankingapplication.filter;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nl.inholland.bankingapplication.util.JWTTokeProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Component
public class JWTFilter extends OncePerRequestFilter {
    private final JWTTokeProvider jwtTokeProvider;

    public JWTFilter(JWTTokeProvider jwtTokeProvider) {
        this.jwtTokeProvider = jwtTokeProvider;
    }

    //gets the filter from the head of th request and check if it matches if it is the same as the one stored and checks the time if it is still validity
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = getToken(request);

// If no token is provided, we'll just continue along the chain.
// The framework will automatically return a 403 if the accessed URL required authorization
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
// If a token was provided, we should validate it and set the security context
// We need a Spring Authentication object to set the Spring Security context
            Authentication authentication = jwtTokeProvider.getAuthentication(token);
// If the token was invalid, the line above will cause an exception

// Set the context, at this point, the user is authenticated
            SecurityContextHolder.getContext().setAuthentication(authentication);

// The exception handling below is not mandatory. If we leave it out, the client will simply receive a 403 status code
// The method below gives us a bit more control, by immediately writing a response and then ending the processing of the request
        } catch (JwtException e) {
// JwtException = something is wrong with the JWT (usually means it's invalid)
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid JWT token");
            response.getWriter().flush();
            return;
        } catch (Exception e) {
// Exception = something else went wrong, we don't know what
// Writing the exception message is probably a bad idea, since it can provide the client with information about potential
// security vulnerabilities. We should log it instead.
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(e.getMessage());
            response.getWriter().flush();
            return;
        }

// Continue along the filter chain
        filterChain.doFilter(request, response);
    }

    private String getToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ") && bearerToken.length() > 7) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
