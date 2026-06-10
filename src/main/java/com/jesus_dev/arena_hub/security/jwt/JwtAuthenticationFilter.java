package com.jesus_dev.arena_hub.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtGenerator jwtGenerator;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtGenerator jwtGenerator, UserDetailsService userDetailsService) {
        this.jwtGenerator = jwtGenerator;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Same contract as for {@code doFilter}, but guaranteed to be
     * just invoked once per request within a single request thread.
     * See {@link #shouldNotFilterAsyncDispatch()} for details.
     * <p>Provides HttpServletRequest and HttpServletResponse arguments instead of the
     * default ServletRequest and ServletResponse ones.
     *
     * @param request
     * @param response
     * @param filterChain
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Se obtiene el token JWT del encabezado "Authorization" de la petición
        String token = getJwtFromRequest(request);

        // Si el token existe, es válido y aún no hay autenticación en el contexto de seguridad
        if (StringUtils.hasText(token)
            && jwtGenerator.validateToken(token)
            && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Se extrae el nombre de usuario desde el token JWT
            String username = jwtGenerator.getUsernameFromJwt(token);

            // Se cargan los datos del usuario desde la base de datos o el sistema de autenticación
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Se crea un objeto de autenticación con los datos del usuario y sus roles/permisos
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );

            // Se agrega información adicional del contexto de la petición
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // Se establece la autenticación en el contexto de seguridad de Spring
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }

        // Se continúa con la cadena de filtros para procesar la solicitud
        filterChain.doFilter(request, response);
    }

    // Se obtiene el JWT desde el encabezado HTTP "Authorization".
    private String getJwtFromRequest(HttpServletRequest request) {
        // Ejemplo del encabezado esperado: "Authorization: Bearer <token>"
        String bearerToken = request.getHeader("Authorization");

        // Si el encabezado contiene texto y empieza con "Bearer ", se devuelve solo el token.
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Elimina la palabra "Bearer " (7 caracteres).
        }

        // Si no cumple con lo anterior, no hay token y se retorna null.
        return null;
    }
}
