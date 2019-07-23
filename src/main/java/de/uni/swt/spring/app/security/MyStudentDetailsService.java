package de.uni.swt.spring.app.security;

import de.uni.swt.spring.backend.data.entity.Admin;
import de.uni.swt.spring.backend.data.entity.Dozent;
import de.uni.swt.spring.backend.data.entity.Student;
import de.uni.swt.spring.backend.repositories.AdminRepository;
import de.uni.swt.spring.backend.repositories.DozentRepository;
import de.uni.swt.spring.backend.repositories.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyStudentDetailsService implements UserDetailsService {
    @Autowired
    private DozentRepository dozentRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private AdminRepository adminRepository;


    @Override
    public UserDetails loadUserByUsername(String email) {
        Student student = studentRepository.findByEmail(email);
        if (student == null) {
            Dozent dozent = dozentRepository.findByEmail(email);
            if (dozent == null) {
                Admin admin = adminRepository.findByEmail(email);
                if (admin == null) {
                    throw new UsernameNotFoundException(email);
                }
                return new MyNutzerPrincipal(admin);
            }
            return new MyNutzerPrincipal(dozent);
        }
        return new MyNutzerPrincipal(student);
    }
}