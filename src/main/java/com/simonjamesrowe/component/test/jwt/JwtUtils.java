package com.simonjamesrowe.component.test.jwt;

import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.CollectionUtils;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class JwtUtils {

    public static String signedJWTTokenRSA256(String userId, String name, String email, String username) throws Exception {
        return signedJWTTokenRSA256(userId, name, email, username, null);
    }

    public static String signedJWTTokenRSA256(String userId, String name, String email, String username, List<String> roles) throws Exception {
        RSAKey rsaJWK = RSAKey.parse(IOUtils.toString(new ClassPathResource("jwk.json").getInputStream(), StandardCharsets.UTF_8));
        JWSSigner signer = new RSASSASigner(rsaJWK);
        SignedJWT signedJWT = new SignedJWT(
                new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(rsaJWK.getKeyID()).type(JOSEObjectType.JWT).build(),
                claims(userId, name, email, username, roles));
        signedJWT.sign(signer);
        return signedJWT.serialize();
    }

    public static String signedJWTTokenHS256(String userId, String name, String email, String username) throws Exception {
        return signedJWTTokenHS256(userId, name, email, username, null);
    }

    public static String signedJWTTokenHS256(String userId, String name, String email, String username, List<String> roles) throws Exception {
        JWSSigner signer = new MACSigner("00000000000000000000000000000000".getBytes());
        SignedJWT signedJWT = new SignedJWT(
                new JWSHeader.Builder(JWSAlgorithm.HS256).type(JOSEObjectType.JWT).build(),
                claims(userId, name, email, username, roles));
        signedJWT.sign(signer);
        return signedJWT.serialize();
    }

    protected static JWTClaimsSet claims(String userId, String name, String email, String username, List<String> roles) {
        JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder()
                .claim("sub", userId)
                .claim("email", email)
                .claim("name", name)
                .claim("username", username)
                .claim("iat", Instant.now().getEpochSecond())
                .claim("exp", Instant.now().plus(Duration.ofMinutes(5)).getEpochSecond());
        if (!CollectionUtils.isEmpty(roles)) {
            Map<String, Object> realmAccess = new HashMap();
            realmAccess.put("roles", roles);
            builder = builder.claim("realm_access", realmAccess);
        }
        return builder.build();
    }

}
