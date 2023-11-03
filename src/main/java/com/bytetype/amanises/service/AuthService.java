package com.bytetype.amanises.service;

import com.bytetype.amanises.exception.NameExistException;
import com.bytetype.amanises.exception.RoleNotFoundException;
import com.bytetype.amanises.exception.UserExistException;
import com.bytetype.amanises.model.Role;
import com.bytetype.amanises.model.RoleType;
import com.bytetype.amanises.model.User;
import com.bytetype.amanises.payload.request.LoginRequest;
import com.bytetype.amanises.payload.request.SignupRequest;
import com.bytetype.amanises.payload.response.UserInfoResponse;
import com.bytetype.amanises.repository.RoleRepository;
import com.bytetype.amanises.repository.UserRepository;
import com.bytetype.amanises.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserInfoResponse authenticateUser(LoginRequest request) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return new UserInfoResponse(userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), roles);
    }

    /**
     * Register a user. If address exist, create a user upon the user.
     * @param request The register request
     * @return The user info
     */
    public UserInfoResponse registerUser(SignupRequest request) throws NameExistException, UserExistException, RoleNotFoundException {
        if (userRepository.existsByUsername(request.getUsername())) throw new NameExistException();

        User user;

        if (userRepository.existsByAddress(request.getAddress())) {
            user = userRepository.findByAddress(request.getAddress()).orElseThrow();
            if (user.getRoles().isEmpty()) {
                user.setUsername(request.getUsername());
                user.setEmail(request.getEmail());
                user.setPassword(passwordEncoder.encode(request.getPassword()));
                user.setAddress(request.getAddress());
            } else throw new UserExistException();
        } else {
            user = User.createFrom(request, passwordEncoder.encode(request.getPassword()));
        }

        Set<String> strRoles = request.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            roles.add(roleRepository.findByName(RoleType.GUEST).orElseThrow(RoleNotFoundException::new));
        } else {
            for (String role : strRoles) {
                switch (role) {
                    case "user":
                        roles.add(roleRepository.findByName(RoleType.USER).orElseThrow(RoleNotFoundException::new));
                        break;
                    case "driver":
                        roles.add(roleRepository.findByName(RoleType.DRIVER).orElseThrow(RoleNotFoundException::new));
                        break;
                    default:
                        roles.add(roleRepository.findByName(RoleType.GUEST).orElseThrow(RoleNotFoundException::new));
                }
            }
        }

        user.setRoles(roles);
        user = userRepository.save(user);

        return new UserInfoResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRoles().stream()
                        .map(role -> role.getName().toString())
                        .collect(Collectors.toList())
        );
    }

    public boolean removeUser(Long id) throws RoleNotFoundException {
        if (userRepository.existsById(id)) {
            User user = userRepository.findById(id).orElse(null);
            if (user == null) return false;

            HashSet<Role> roles = new HashSet<>();
            roles.add(roleRepository.findByName(RoleType.GUEST).orElseThrow(RoleNotFoundException::new));

            user.setPassword(null);
            user.setRoles(roles);

            return true;
        } else {
            return false;
        }
    }
}