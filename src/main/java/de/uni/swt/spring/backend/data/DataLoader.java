package de.uni.swt.spring.backend.data;

import de.uni.swt.spring.backend.data.entity.Admin;
import de.uni.swt.spring.backend.repositories.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataLoader {

    private PasswordEncoder passwordEncoder;
    private AdminRepository adminRepository;

    @Autowired
    public DataLoader(AdminRepository adminRepository, PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
        LoadAdmin();
    }

    private void LoadAdmin() {
        Admin admin = new Admin();
        admin.setEmail("admin@uni-rostock.de");
        admin.setPasswort(passwordEncoder.encode("admin"));
        admin.setRole("Admin");
        admin.setPasswortChanged(false);
        adminRepository.save(admin);
    }
}
