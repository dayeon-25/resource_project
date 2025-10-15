package com.example.resource.service;

import com.example.resource.dto.SignupRequest;
import com.example.resource.entity.Member;
import com.example.resource.exception.SignupException;
import com.example.resource.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }
    @Transactional
    public void register(SignupRequest request) {
        if (memberRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new SignupException("이미 사용 중인 아이디입니다.");
        }

        try {
            Member member = Member.builder()
                    .username(request.getUsername())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .name(request.getName())
                    .birthday(request.getBirthday())
                    .userType(request.getUserType())
                    .build();
            memberRepository.save(member);
        } catch (Exception e) {
            throw new SignupException("회원가입 중 오류가 발생했습니다.");
        }
    }

}
