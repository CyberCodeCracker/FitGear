package com.amouri_coding.FitGear.user.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    // GET /admin/dashboard
    @GetMapping("/dashboard")
    public ResponseEntity<AdminDashboardResponse> getDashboard() {
        return ResponseEntity.ok(adminService.getDashboard());
    }

    // GET /admin/users?page=0&size=20&search=john&role=COACH
    @GetMapping("/users")
    public ResponseEntity<Page<AdminUserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0")  int    page,
            @RequestParam(defaultValue = "20") int    size,
            @RequestParam(defaultValue = "")   String search,
            @RequestParam(defaultValue = "")   String role) {
        return ResponseEntity.ok(adminService.getAllUsers(page, size, search, role));
    }

    // GET /admin/users/{id}
    @GetMapping("/users/{id}")
    public ResponseEntity<AdminUserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getUserById(id));
    }

    // PUT /admin/users/{id}/lock  → toggle locked/unlocked
    @PutMapping("/users/{id}/lock")
    public ResponseEntity<AdminUserResponse> toggleLock(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.toggleLock(id));
    }

    // PUT /admin/users/{id}/enable  → toggle enabled/disabled
    @PutMapping("/users/{id}/enable")
    public ResponseEntity<AdminUserResponse> toggleEnable(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.toggleEnable(id));
    }

    // DELETE /admin/users/{id}
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
