package com.mimaja.job_finder_app.security.token.accessToken.authorizationFilter;

import com.mimaja.job_finder_app.shared.record.JwtPrincipal;
import java.util.Collection;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class AuthenticationToken extends AbstractAuthenticationToken {
    private final JwtPrincipal principal;

    public AuthenticationToken(
            JwtPrincipal principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }
}
