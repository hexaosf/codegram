package org.electronic.electronicdocumentsystemjava.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.electronic.electronicdocumentsystemjava.entity.User;
import org.electronic.electronicdocumentsystemjava.service.IUserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtManager {
    @Value("${jwt.expire_time:86400}")
    private Integer EXPIRE_TIME;
    @Value("${jwt.secret}")
    private String SECRET;

    private final IUserService userService;

    public JwtManager(IUserService userService) {
        this.userService = userService;
    }

    public String encrypt(User user) {
        Map<String, String> payload = new HashMap<>();
        payload.put("id", String.valueOf(user.getId()));
        payload.put("username", user.getUsername());
        return encrypt(payload);
    }

    public String encrypt(Map<String, String> payload) {
        Map<String, Object> headers = new HashMap<>();
        Calendar expires = Calendar.getInstance();
        expires.add(Calendar.SECOND, EXPIRE_TIME);

        JWTCreator.Builder builder = JWT.create();
        // 第一部分Header
        builder.withHeader(headers);
        // 第二部分Payload
        payload.forEach(builder::withClaim);
        // 到期时间
        builder.withExpiresAt(expires.getTime());
        // 第三部分Signature
        return builder.sign(Algorithm.HMAC256(SECRET));
    }

    public User decrypt(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET);
            DecodedJWT jwt = JWT.require(algorithm).build().verify(token);
            Integer userId = Integer.parseInt(jwt.getClaim("id").asString());
            return userService.getById(userId);
        } catch (Exception e) {
            return null;
        }
    }
}
