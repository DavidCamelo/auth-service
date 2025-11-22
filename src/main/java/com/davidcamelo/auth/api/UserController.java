package com.davidcamelo.auth.api;
import com.davidcamelo.auth.entity.User;
import com.davidcamelo.auth.repository.RoleRepository;
import com.davidcamelo.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/users")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class UserController {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @PostMapping("/{username}/roles/{roleName}")
    public ResponseEntity<User> assignRoleToUser(@PathVariable String username, @PathVariable String roleName) {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        var role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        user.getRoles().add(role);
        userRepository.save(user);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{username}/roles/{roleName}")
    public ResponseEntity<User> removeRoleFromUser(@PathVariable String username, @PathVariable String roleName) {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        var role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        user.getRoles().remove(role);
        userRepository.save(user);
        return ResponseEntity.ok(user);
    }
}