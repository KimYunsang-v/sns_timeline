package com.hackday.sns_timeline.sign.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hackday.sns_timeline.error.customException.DuplicationException;
import com.hackday.sns_timeline.error.customException.RepositoryNullException;
import com.hackday.sns_timeline.searchMember.domain.document.SearchMemberDoc;
import com.hackday.sns_timeline.searchMember.repository.SearchMemberDocRepository;
import com.hackday.sns_timeline.sign.domain.dto.CustomUser;
import com.hackday.sns_timeline.sign.domain.dto.MemberDto;
import com.hackday.sns_timeline.sign.domain.entity.Member;
import com.hackday.sns_timeline.sign.repository.MemberRepository;
import com.hackday.sns_timeline.subscribe.service.SubscribeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@RequiredArgsConstructor
public class SignService implements UserDetailsService {

	final private MemberRepository memberRepository;
	final private SubscribeService subscribeService;
	final private SearchMemberDocRepository searchMemberDocRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Member member = memberRepository.findByEmail(username)
			.orElseThrow(() -> new UsernameNotFoundException(username));

		List<GrantedAuthority> authorities =
			member.getRoles().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());

		return new CustomUser(member.getEmail(), member.getPassword(), authorities, member.getId(), member.getName());
	}

	@Transactional
	public Member signUp(MemberDto memberDto) throws DuplicationException {
		if(memberRepository.findByEmail(memberDto.getEmail()).isPresent()){
			throw new DuplicationException("email already exist");
		}

		Member member = memberRepository.save(Member.buildMember(memberDto));

		if(searchMemberDocRepository.findByMemberId(member.getId()).isPresent()){
			throw new DuplicationException("member id already exist");
		}

		// roll back 구현 해야함
		searchMemberDocRepository.save(
			SearchMemberDoc.builder()
			.email(member.getEmail())
			.name(member.getName())
			.memberId(member.getId()).build());

		subscribeService.addSubscribe(member.getId(), member.getId());

		return member;
	}

	public void signDrop(MemberDto memberDto) throws DuplicationException {
		Member member = memberRepository.findByEmail(memberDto.getEmail())
			.orElseThrow(() -> new RepositoryNullException("memberRepository error"));
		memberRepository.delete(member);


		SearchMemberDoc searchMemberDoc = searchMemberDocRepository.findByMemberId(member.getId())
			.orElseThrow(() -> new RepositoryNullException("memberRepository error"));

		searchMemberDocRepository.delete(searchMemberDoc);
	}
}
