package org.kate.claimservice.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import java.util.Collection;
import java.util.List;

public class JwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    public AbstractAuthenticationToken convert(@NonNull Jwt source) {
        // 1. Extract the "role" claim we added in JwtService
        String role = source.getClaimAsString("role");
        // 2. Convert the String into a GrantedAuthority object
        // If role is null, we provide an empty list to avoid errors
        Collection<SimpleGrantedAuthority> authorities = (role != null)
                ? List.of(new SimpleGrantedAuthority(role))
                : List.of();

        // 3. Return the token that Spring Security uses to authorize @PreAuthorize
        return new JwtAuthenticationToken(source, authorities);
    }
}