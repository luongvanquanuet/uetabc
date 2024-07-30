package com.abc.configuration;

import java.text.ParseException;
import java.util.Objects;
import javax.crypto.spec.SecretKeySpec;

import com.abc.dto.request.IntrospectRequest;
import com.abc.service.AuthenticationService;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import com.nimbusds.jose.JOSEException;

@Component
public class CustomJwtDecoder implements JwtDecoder {
    @Value("${application.security.jwt.signerKey}")
    //@NonFinal
    private String signerKey ="r5vzbhqhmf9asnu1edzdh0bibutew513r1ty84g4y866bwveo21pyco63mamqabu";

    @Autowired
    private AuthenticationService authenticationService;

    private NimbusJwtDecoder nimbusJwtDecoder = null;
//Hàm decode trong đoạn mã của bạn được sử dụng để giải mã (decode)
// một JSON Web Token (JWT) và trả về đối tượng Jwt tương ứng
    @Override
    public Jwt decode(String token) throws JwtException {

        try {
            var response = authenticationService.Introspect(
                    IntrospectRequest.builder().token(token).build());

            if (!response.isValid()) throw new JwtException("Token invalid");
        } catch (JOSEException | ParseException e) {
            throw new JwtException(e.getMessage());
        }
        //Nếu token không hợp lệ (!response.isValid()), JwtException sẽ được ném ra ngay lập tức: throw new JwtException("Token invalid");.
        //Tại điểm này, ngoại lệ JwtException không được khối catch bắt lại, vì khối catch chỉ bắt các ngoại lệ JOSEException và ParseException.
        //Do đó, phương thức decode sẽ kết thúc và không thực hiện các dòng mã phía dưới trong khối try.
        if (Objects.isNull(nimbusJwtDecoder)) {
            SecretKeySpec secretKeySpec = new SecretKeySpec(signerKey.getBytes(), "HS512");
            nimbusJwtDecoder = NimbusJwtDecoder.withSecretKey(secretKeySpec)
                    .macAlgorithm(MacAlgorithm.HS512)
                    .build();
        }

        return nimbusJwtDecoder.decode(token);
        // sử dụng nimbusJwtDecoder để giải mã JWT và trả về đối tượng Jwt tương ứng.
    }
}