package com.hackday.sns_timeline.profile.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hackday.sns_timeline.error.customException.RepositoryNullException;
import com.hackday.sns_timeline.sign.domain.dto.MemberDto;
import com.hackday.sns_timeline.sign.domain.entity.Member;
import com.hackday.sns_timeline.sign.repository.MemberRepository;
import com.hackday.sns_timeline.subscribe.domain.document.SubscribeDoc;
import com.hackday.sns_timeline.subscribe.repository.SubscribeDocRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@RequiredArgsConstructor
public class ProfileService {

	private final MemberRepository memberRepository;

	private final SubscribeDocRepository subscribeDocRepository;

	@Transactional
	public Member getMemberProfile(long userId) throws RepositoryNullException {
		return memberRepository.findById(userId)
			.orElseThrow(() -> new RepositoryNullException("memberRepository error"));
	}

	@Transactional
	public List<MemberDto> getSubscribeMember(Member member){
		List<SubscribeDoc> subscribeDocList = subscribeDocRepository.findByMemberId(member.getId());

		return subscribeDocList.stream().map(MemberDto::subscribeEsConverter).collect(Collectors.toList());
	}
}
