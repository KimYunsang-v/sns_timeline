package com.hackday.sns_timeline.sign.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

import com.hackday.sns_timeline.sign.domain.entity.Member;

@EnableJpaRepositories
public interface MemberRepository extends JpaRepository<Member, Long> {

	@Override
	Optional<Member> findById(Long aLong);

	Optional<Member> findByEmail(String email);

	@Query(value = "select * from member member where member.email like %:search% or member.name like %:search%", nativeQuery = true)
	Page<Member> searchMember(@Param("search") String search, Pageable pageable);

	@Query(value = "select Max(member.id) from member member", nativeQuery = true)
	Long findMaxId();
}
