package msg.flight.manager.services;


import msg.flight.manager.persistence.dtos.user.auth.AuthenticationRequest;
import msg.flight.manager.persistence.dtos.user.auth.AuthenticationResponse;
import msg.flight.manager.persistence.repositories.UserRepository;
import msg.flight.manager.security.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthenticationService {
    @Autowired
    JWTService jwtService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Transactional
    public ResponseEntity<AuthenticationResponse> login(AuthenticationRequest authenticationRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())
        );

        var user = userRepository.findByUsername(authenticationRequest.getUsername()).get();
        var jwtToken = jwtService.generateToken(user);
        AuthenticationResponse response = AuthenticationResponse.builder().token(jwtToken).username(user.getUsername()).role(user.getRole()).build();
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<String> logout(String token) {
        SecurityContextHolder.clearContext();
            if(jwtService.rejectToken(token.substring(7)) > 0) {
                return ResponseEntity.ok("disconnected");
            }else{
                return  new ResponseEntity<String>("still connected", HttpStatus.BAD_REQUEST);
            }
    }
}
