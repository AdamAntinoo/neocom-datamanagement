package org.dimensinfin.eveonline.neocom;

import net.troja.eve.esi.ApiClient;
import net.troja.eve.esi.ApiClientBuilder;
import net.troja.eve.esi.ApiException;
import net.troja.eve.esi.auth.OAuth;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static net.troja.eve.esi.auth.SsoScopes.ESI_INDUSTRY_READ_CHARACTER_MINING_V1;

public class SsoEsiTest {
    private final String state = "somesecret";
    private OAuth auth;

    @BeforeEach
    void setUp() {
        final String clientId = "98eb8d31c5d24649ba4f7eb015596fbd";
        final ApiClient client = new ApiClientBuilder().clientID(clientId).build();
        auth = (OAuth) client.getAuthentication("evesso");
    }
@Disabled
    @Test
    public void doSsoEsiAuthenticationTest() throws IOException, URISyntaxException, ApiException {
        final String[] testValues = {
                ESI_INDUSTRY_READ_CHARACTER_MINING_V1
        };
        final Set<String> scopes = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(testValues)));
        final String redirectUri = "http://localhost:32000/app/loginValidation";
        final String authorizationUri = auth.getAuthorizationUri(redirectUri, scopes, state);
        System.out.println("Authorization URL: " + authorizationUri);
        System.out.println("Code verifier: " + auth.getCodeVerifier());
        Desktop.getDesktop().browse(new URI(authorizationUri));
    }
@Disabled
    @Test
    public void getARefreshToken() throws ApiException {
        final String code = "LOjG3novykec9z3ufVsptA";
        final String[] testValues = {
                ESI_INDUSTRY_READ_CHARACTER_MINING_V1
        };
        final Set<String> scopes = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(testValues)));
        final String redirectUri = "http://localhost:32000/app/loginValidation";
        final String authorizationUri = auth.getAuthorizationUri(redirectUri, scopes, state);
        final String codeVerifier = auth.getCodeVerifier();
        System.out.print("Code from Answer:" + code);
        auth.finishFlow(code, state, codeVerifier);
        System.out.println("Refresh Token: " + auth.getRefreshToken());
    }
}
