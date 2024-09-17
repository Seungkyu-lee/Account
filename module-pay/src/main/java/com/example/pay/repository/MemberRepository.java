package com.example.pay.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.pay.domain.MemberEntity;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
	Optional<MemberEntity> findByUsername(String username);

	boolean existsByUsername(String username);
}

