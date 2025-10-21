package com.QueroTrabalhar.controllers;

import com.QueroTrabalhar.dtos.LoginResponseDTO;
import com.QueroTrabalhar.dtos.user.UserDTOLogin;
import com.QueroTrabalhar.entity.Usuario;
import com.QueroTrabalhar.security.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
public class AuthenticationController {

    @Autowired
    TokenService tokenService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity login (@RequestBody @Valid UserDTOLogin data){
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.senha());
        var authentication = this.authenticationManager.authenticate(usernamePassword);

        var token = tokenService.generateToken((Usuario) authentication.getPrincipal());

        return ResponseEntity.ok(new LoginResponseDTO(token));
    }
}
