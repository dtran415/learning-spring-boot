package com.mshift.acf.auth.application_user;

import com.mshift.acf.auth.jwt.JwtHelper;
import com.mshift.acf.auth.jwt.refresh_token.RefreshTokenService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.time.LocalDateTime;

import static com.mshift.acf.auth.security.ApplicationUserRole.BASEUSER;

@RestController
public class ApplicationUserController {

    private final ApplicationUserService applicationUserService;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final JwtHelper jwtHelper;
    private final RefreshTokenService refreshTokenService;

    @Autowired
    public ApplicationUserController(ApplicationUserService applicationUserService, PasswordEncoder passwordEncoder, ModelMapper modelMapper, JwtHelper jwtHelper, RefreshTokenService refreshTokenService) {
        this.applicationUserService = applicationUserService;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
        this.jwtHelper = jwtHelper;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping(path = "register")
    @Transactional
    public ApplicationUserDTO createUser(@RequestBody @Valid ApplicationUser body, HttpServletResponse response) {

        String username = body.getUsername();
        String password = body.getPassword();
        password = passwordEncoder.encode(password);

        ApplicationUser user = new ApplicationUser(LocalDateTime.now(), LocalDateTime.now(),
                username, password, BASEUSER.getGrantedAuthorities(),
                true,true, true, true);

        ApplicationUser createdUser = applicationUserService.createUser(user);
        ApplicationUserDTO dto = modelMapper.map(createdUser, ApplicationUserDTO.class);

        jwtHelper.generateTokens(response, refreshTokenService, username, createdUser.getAuthorities(), false);

        return dto;
    }

    @DeleteMapping(path = "/user/{id}")
    public void deleteUser(@PathVariable String id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();

        ApplicationUser userDetails = (ApplicationUser)applicationUserService.loadUserByUsername(name);
        if (userDetails.getId().equals(id)) {
            applicationUserService.deleteUserById(id);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

}
