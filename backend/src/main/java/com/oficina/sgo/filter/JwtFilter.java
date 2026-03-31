package com.oficina.sgo.filter;

import com.oficina.sgo.model.User;
import com.oficina.sgo.security.JwtTokenProvider;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebFilter(filterName = "JwtFilter", urlPatterns = "/api/*")
public class JwtFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String path = req.getServletPath();
        if (path.startsWith("/api/auth")) {
            chain.doFilter(request, response);
            return;
        }
        
        String header = req.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().write("{\"status\":401,\"message\":\"Authentication required\"}");
            return;
        }

        String token = header.substring(7);
        JwtTokenProvider jwtProvider = (JwtTokenProvider) req.getServletContext().getAttribute("jwtTokenProvider");

        if (jwtProvider == null || !jwtProvider.validateToken(token)) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().write("{\"status\":401,\"message\":\"Invalid or expired token\"}");
            return;
        }

        String username = jwtProvider.getUsernameFromToken(token);
        String role = jwtProvider.getRoleFromToken(token);

        EntityManagerFactory emf = (EntityManagerFactory) req.getServletContext().getAttribute("emf");
        if (emf != null) {
            try (EntityManager em = emf.createEntityManager()) {
                List<User> users = em.createQuery(
                        "SELECT u FROM User u WHERE u.username = :username AND u.active = true", User.class)
                        .setParameter("username", username).getResultList();
                if (!users.isEmpty()) {
                    req.setAttribute("currentUser", users.get(0));
                }
            }
        }

        req.setAttribute("currentUsername", username);
        req.setAttribute("currentRole", role);

        chain.doFilter(request, response);
    }
}
