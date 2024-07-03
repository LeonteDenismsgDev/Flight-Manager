package msg.flight.manager.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import msg.flight.manager.persistence.models.user.DBToken;
import msg.flight.manager.persistence.repositories.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Service
public class JWTService {

    @Autowired
    TokenRepository tokenRepository;

    private static final String SECRET_KEY = "b30dfcb94c7a22933d7af83be9031cfd9c29bf7de89d87f9c7f5377314f01aa6";

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getSignIndKey()).build().parseClaimsJws(token).getBody();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Key getSignIndKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        LocalDateTime issuedAt = LocalDateTime.now();
        LocalDateTime expirationDate =  issuedAt.plusDays(1);
        String token = Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(Timestamp.valueOf(issuedAt))
                .setExpiration(Timestamp.valueOf(expirationDate))
                .signWith(getSignIndKey(), SignatureAlgorithm.HS256)
                .compact();
        tokenRepository.save(creteDBToken(token,userDetails.getUsername(),issuedAt,expirationDate));
        return token;
    }

    private DBToken creteDBToken(String token, String username,LocalDateTime issuedAt,LocalDateTime expirationDate){
        return DBToken.builder()
                .token(token)
                .username(username)
                .issuedAT(issuedAt)
                .expirationDate(expirationDate)
                .rejected(false)
                .build();
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        final Boolean rejected = tokenRepository.isTokenRejected(token);
        if(rejected != null){
            return username.equals(userDetails.getUsername()) && !isTokenExpired(token) && !rejected;
        }
        return false;
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public long  rejectToken(String token){
       return tokenRepository.rejectToken(token);
    }
}
