package com.mimaja.job_finder_app.feature.unit.security.authorization.googleAuth.mock;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;

import java.security.GeneralSecurityException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class GoogleIdTokenMock {
    private final GoogleIdTokenVerifier verifier;
    private final GoogleIdToken googleIdToken;
    private final GoogleIdToken.Payload payload;

    public GoogleIdTokenMock(GoogleIdTokenVerifier verifier, GoogleIdToken googleIdToken, GoogleIdToken.Payload payload) {
        this.verifier = verifier;
        this.googleIdToken = googleIdToken;
        this.payload = payload;
    }

    public void setupSuccessfulTokenVerification(String googleId, String email) throws Exception {
        when(verifier.verify(anyString())).thenReturn(googleIdToken);
        when(googleIdToken.getPayload()).thenReturn(payload);
        when(payload.getSubject()).thenReturn(googleId);
        when(payload.getEmail()).thenReturn(email);
    }

    public void setupFailedTokenVerification() throws Exception {
        when(verifier.verify(anyString()))
                .thenThrow(new GeneralSecurityException("Invalid token"));
    }

    public void setupNullTokenVerification() throws Exception {
        when(verifier.verify(anyString())).thenReturn(null);
    }

    public void setupIOExceptionTokenVerification() throws Exception {
        when(verifier.verify(anyString()))
                .thenThrow(new java.io.IOException("Token verification failed"));
    }
}