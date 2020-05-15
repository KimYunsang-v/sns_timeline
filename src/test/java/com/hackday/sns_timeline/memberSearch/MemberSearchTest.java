package com.hackday.sns_timeline.memberSearch;


import static org.assertj.core.api.Assertions.*;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import com.hackday.sns_timeline.sign.domain.dto.MemberDto;
import com.hackday.sns_timeline.sign.domain.entity.Member;
import com.hackday.sns_timeline.sign.repository.MemberRepository;
import com.hackday.sns_timeline.sign.service.SignService;
import com.hackday.sns_timeline.subscribe.controller.SubscribeController;

@SpringBootTest
public class MemberSearchTest {

	@Autowired
	MemberRepository memberRepository;

	@Autowired
	SignService signService;


	@Before
	public void before() {

	}


	@Test
	@Transactional
	public void getMemberListSuccessTest() throws Exception {
		String name = "newTest";
		int count = 13;
		for (int i = 0; i < count; i++) {
			signService.signUp(MemberDto.builder()
				.email(name+i + "@test")
				.name(name+i)
				.password("test")
				.build());
		}

		Pageable pageable = PageRequest.of(0, 10);
		Page<Member> members = memberRepository.searchMember(name, pageable);

		assertThat(members.getContent().size()).isEqualTo(10);
		assertThat(members.getTotalElements()).isEqualTo(count);
	}

	@Test
	@Transactional
	public void searchMemberListFailTest(){
		String search = "noData";
		Pageable pageable = PageRequest.of(0, 10);
		Page<Member> members = memberRepository.searchMember(search, pageable);

		assertThat(members.getContent().size()).isEqualTo(0);
		assertThat(members.getTotalElements()).isEqualTo(0);
	}

}
