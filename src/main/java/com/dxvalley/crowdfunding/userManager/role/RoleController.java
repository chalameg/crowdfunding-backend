package com.dxvalley.crowdfunding.userManager.role;

import com.dxvalley.crowdfunding.userManager.authority.Authority;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

    @GetMapping
    public ResponseEntity<List<Role>> getRoles() {
        return ResponseEntity.ok(roleService.getRoles());
    }

    @PostMapping("/add")
    public ResponseEntity<Role> addRole(@RequestBody @Validated RoleDTO roleDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roleService.addRole(roleDTO));
    }

    @PutMapping("/addAuthorityToRole/{roleId}")
    public ResponseEntity<Role> addAuthorityToRole(@PathVariable Short roleId, @RequestBody Authority authority) {
        return ResponseEntity.ok(roleService.addAuthorityToRole(roleId, authority));
    }

    @PutMapping("/removeAuthorityFromRole/{roleId}")
    public ResponseEntity<Role> removeAuthorityFromRole(@PathVariable Short roleId, @RequestBody Authority authority) {
        return ResponseEntity.ok(roleService.removeAuthorityFromRole(roleId, authority));
    }
}
