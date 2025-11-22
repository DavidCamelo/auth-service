package com.davidcamelo.auth.api;
import com.davidcamelo.auth.dto.RoleDTO;
import com.davidcamelo.auth.entity.Role;
import com.davidcamelo.auth.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth/roles")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')") // Secure all endpoints in this controller
public class RoleController {
    private final RoleRepository roleRepository;

    @PostMapping
    public ResponseEntity<Role> createRole(@RequestBody RoleDTO roleDto) {
        if (roleRepository.findByName(roleDto.name()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        var newRole = new Role();
        newRole.setName(roleDto.name());
        return ResponseEntity.status(HttpStatus.CREATED).body(roleRepository.save(newRole));
    }

    @GetMapping
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable Integer id) {
        if (!roleRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        // Add logic here to handle users with this role before deleting
        roleRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}