package com.abc.service;
import java.text.ParseException;
import java.time.temporal.ChronoUnit;
import com.abc.dto.request.AuthenticationRequest;
import com.abc.dto.request.IntrospectRequest;
import com.abc.dto.request.LogoutRequest;
import com.abc.dto.response.AuthenticationResponse;
import com.abc.dto.response.IntrospectResponse;
import com.abc.dto.response.UsersResponse;
import com.abc.entity.InvalidatedToken;
import com.abc.entity.Users;
import com.abc.exception.AppException;
import com.abc.exception.ErrorCode;
import com.abc.repository.InvalidatedTokenRepository;
import com.abc.repository.UsersRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

@Service
@RequiredArgsConstructor

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
   // @NonFinal
    //protected static final String Key = "U#j?KKEV3nPTdscSb?CvThMKcJ943Shb";
    @Value("${application.security.jwt.signerKey}")
    @NonFinal
    private String signerKey ="r5vzbhqhmf9asnu1edzdh0bibutew513r1ty84g4y866bwveo21pyco63mamqabu";
    //signerKey là một chuỗi biểu diễn cho khóa bí mật được sử dụng để ký JWT.
    UsersRepository usersRepository;
    InvalidatedTokenRepository invalidatedTokenRepository;

    public IntrospectResponse Introspect1(IntrospectRequest request) throws JOSEException, ParseException {
        var token = request.getToken();
        JWSVerifier verifier = new MACVerifier(signerKey.getBytes());
        // Tạo một đối tượng JWSVerifier để xác minh chữ ký của token.
        // MACVerifier sử dụng khóa bí mật (Key_1) được chuyển đổi thành mảng byte.
        SignedJWT signedJWT = SignedJWT.parse(token);
        //phân tích chuỗi token thành một đối tượng SignedJWT.
        // Điều này cho phép truy xuất các phần của JWT như header, payload và chữ ký.
        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier);
        //sử dụng đối tượng verifier đã khởi tạo (là MACVerifier được tạo
        // từ signerKey) để xác minh chữ ký của JWT (signedJWT). Kết quả xác minh (true/false) được lưu vào biến verified
        // Xác minh chữ ký của token bằng cách sử dụng verifier đã tạo trước đó.
        // Điều này đảm bảo rằng token chưa bị thay đổi kể từ khi nó được phát hành.

        //if (!()) throw new AppException(ErrorCode.UNAUTHENTICATED);

        return IntrospectResponse.builder()
                .valid(verified && expiryTime.after(new Date()))
                .build();
    }

    public IntrospectResponse Introspect(IntrospectRequest request) throws JOSEException, ParseException {
        var token = request.getToken();
        boolean isValid = true;

        try {
            verifyToken(token, false);
        } catch (AppException e) {
            isValid = false;
        }

        //var verified = signedJWT.verify(verifier);
        return IntrospectResponse.builder()
                .valid(isValid)
                .build();
    }
    public AuthenticationResponse authenticate(AuthenticationRequest request){
        //var users = usersRepository.findByUsername(request.getUsername()).orElseThrow(()-> new AppException(ErrorCode.USER_NOT_EXISTED));
        var users = usersRepository
                .findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        //boolean pass = passwordEncoder.matches(request.getPassword(),users.getPassword());
       boolean authenticated =  passwordEncoder.matches(request.getPassword(),users.getPassword());
       if(!authenticated){
           throw new AppException(ErrorCode.UNAUTHENTICATED);
       }
    try {
            var token_1 = generateToken(users);
            return AuthenticationResponse.builder()
                    .token(token_1)
            .authenticated(true)
                .build();
        } catch (KeyLengthException e) {// độ dài của khóa mã hóa. Đây là một ngoại lệ
        // cụ thể để báo hiệu rằng chiều dài của khóa không đáp ứng các yêu cầu cần thiết cho một thuật toán mã hóa nhất định.
            throw new RuntimeException(e);
        }
        /*var token_1 = generateToken(request.getUsername());
        return AuthenticationResponse.builder()
                .token(token_1)
                .authenticated(true)
                .build();*/
    }

    public void logout(LogoutRequest request) throws ParseException, JOSEException {
        try {
            var signToken = verifyToken(request.getToken(), true);

            String jit = signToken.getJWTClaimsSet().getJWTID();
            Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();

            InvalidatedToken invalidatedToken =
                    InvalidatedToken.builder().id(jit).expiryTime(expiryTime).build();

            invalidatedTokenRepository.save(invalidatedToken);
        } catch (AppException exception){
            log.info("Token already expired");
        }
    }

    private SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(signerKey.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier);

        if (!(verified && expiryTime.after(new Date()))) throw new AppException(ErrorCode.UNAUTHENTICATED);

        if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID()))
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        return signedJWT;
    }
    //tao tocken
    private String generateToken(Users users) throws KeyLengthException {
        //Tạo một header cho JWT với thuật toán ký HMAC sử dụng SHA-512 (HS512).
        JWSHeader jwsHeader =  new JWSHeader(JWSAlgorithm.HS512);
        //body se gui di trong token
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(users.getUsername())

                .issuer("com.abc")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()

                ))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", buildScope(users))//Thêm thông tin về quyền hạn (scope) của người dùng vào token.
                .build();
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());// Tạo payload từ đối tượng JWTClaimsSet
        JWSObject jwsObject = new JWSObject(jwsHeader,payload);
        //Ký token bằng khóa bí mật:
        try {
            jwsObject.sign(new MACSigner(signerKey.getBytes()));
            //signerKey.getBytes()từ kiểu String sang mảng byte (byte[]
            //MacSigner được sử dụng để ký và xác thực các đối tượng JSON Web Token (JWT) bằng cách sử dụng thuật toán HMAC-SHA.
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
        return jwsObject.serialize();// chuyen token thanh kieu string

        }

    private String buildScope(Users user) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        if (!CollectionUtils.isEmpty(user.getRoles()))
            user.getRoles().forEach(role -> {
               // stringJoiner.add("ROLE_" + role.getName());
                stringJoiner.add("ROLE_"+role.getName());
                if (!CollectionUtils.isEmpty(role.getPermissions()))
                    role.getPermissions().forEach(permission -> stringJoiner.add(permission.getName()));
            });
       /* if (!CollectionUtils.isEmpty(user.getRoles()))
            user.getRoles().forEach(s -> {

            });   stringJoiner.add(s);
*/

        return stringJoiner.toString();
    }

    }

