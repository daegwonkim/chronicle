package io.github.daegwonkim.chronicle.service;

import io.github.daegwonkim.chronicle.dto.admins.SignInDto;
import io.github.daegwonkim.chronicle.dto.admins.SignUpDto;
import io.github.daegwonkim.chronicle.entity.Admin;
import io.github.daegwonkim.chronicle.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;

    @Transactional
    public void signUp(SignUpDto.Req req) {
        boolean exists = adminRepository.existsByUsernameAndPasswordAndWithdrawnFalse(req.username(), req.password());

        if (exists) {
            throw new IllegalArgumentException("이미 존재하는 계정");
        }

        adminRepository.save(Admin.create(req.username(), req.password()));
    }

    @Transactional(readOnly = true)
    public void signIn(SignInDto.Req req) {
        adminRepository.findByUsernameAndPasswordAndWithdrawnFalse(req.username(), req.password())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 계정"));
    }
}
