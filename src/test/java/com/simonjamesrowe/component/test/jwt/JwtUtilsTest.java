package com.simonjamesrowe.component.test.jwt;

import com.nimbusds.jwt.JWTClaimsSet;
import com.simonjamesrowe.component.test.BaseComponentTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


class JwtUtilsTest extends BaseComponentTest {

    @Value("http://localhost:${wiremock.server.port}/auth/realms/master/protocol/openid-connect/certs")
    private String jwksUrl;

    @Test
    public void testCanGenerateJwtWithRSA256Signer() throws Exception {
        assertThat(JwtUtils.signedJWTTokenRSA256(UUID.randomUUID().toString(), "Simon Rowe", "simon@y-tree.com", "simon@y-tree.com")).isNotBlank();
    }

    @Test
    public void testCanGenerateJwtWithRSA256SignerH256Signer() throws Exception {
        assertThat(JwtUtils.signedJWTTokenHS256(UUID.randomUUID().toString(), "Simon Rowe", "simon@y-tree.com", "simon@y-tree.com")).isNotNull();
    }

    @Test
    public void testWireMockCertsEndpointExists() {
        String jwkResponse = new RestTemplate().getForObject(jwksUrl, String.class);
        assertThat(jwkResponse).isEqualTo("{\"keys\":[{\"kty\":\"RSA\",\"d\":\"mcjJ4jEPxvcR3UmRoPPfxQXkCPHbpcui-tNqTfdGdn0SDcILZWUWW0MSGgdAHEPWGr50S0lgOJLjTFJPvA1WzHlQHBfcaXgb2Sob4wu-8BVsHKRKX3BAD57BHPcJVYcvlQhHd5TRt0Xq_4ovC9T-0ZMydVtlObpIiGSJiT-rFBV0JqM8jY-_vJLr6WcK3uwbkEWJh9IBD8JahCayA6Wb0X8gBlE4lh-ZNpY4IfvmyLq0yLaB0eRhBpho9I1zFaxF70B64ki7NmptTFZAcxNYCrWw-YlTGYPG0uD01rwTlUtNXDwSoYR3nUgf4w4IAAAFXRs-AiMOwuEeYn5Asx9F_Q\",\"e\":\"AQAB\",\"use\":\"sig\",\"kid\":\"sampleKey\",\"alg\":\"RS256\",\"n\":\"5ctuWiCF3_7WZBNxMqHQezb14LysywynLsvvjEx9MyhXheqP4EAKdjSNNl3nllofqaQESGMbgkqnLOyatvIqa-DDrxm_3EII2C-0hi0bfCKaKgojhY6z_0TxmUiJZ1Je67RPZ3jfOfbHgGmdtPkQUULfl0wNXIGFo7OkNTdRbdHh9BimBI5-8y3v9mOZ6rlntWwJn7_9HEWEv_hD_tX0ZNAkylVQu-zVokLyxSYqtYc_uCblZ9M3XND4jDa_oouYKb9rcSI0wrFqSpQSUd33LpYekhVHU14xMqmj92xXJKuapwpbd2d0hWTIiI89kxDuhOmvs2sVWyvubB_lqd5-iQ\"}]}");
    }

    @Test
    public void testClaims() throws Exception {
        JWTClaimsSet claimsSet = JwtUtils.claims(UUID.randomUUID().toString(), "Simon Rowe", "simon@y-tree.com", "simon@y-tree.com", null);
        assertThat(claimsSet.getJSONObjectClaim("realm_access")).isNull();

        claimsSet = JwtUtils.claims(UUID.randomUUID().toString(), "Simon Rowe", "simon@y-tree.com", "simon@y-tree.com", Arrays.asList("actuator"));
        assertThat(claimsSet.getJSONObjectClaim("realm_access")).isNotNull();
        assertThat(claimsSet.getJSONObjectClaim("realm_access")).hasFieldOrPropertyWithValue("roles", Arrays.asList("actuator"));
    }
}