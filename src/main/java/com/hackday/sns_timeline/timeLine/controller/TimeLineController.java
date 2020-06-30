package com.hackday.sns_timeline.timeLine.controller;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.hackday.sns_timeline.common.CommonConst;
import com.hackday.sns_timeline.content.domain.dto.ContentDto;
import com.hackday.sns_timeline.content.service.ContentService;
import com.hackday.sns_timeline.profile.service.ProfileService;
import com.hackday.sns_timeline.sign.domain.dto.CustomUser;
import com.hackday.sns_timeline.sign.domain.dto.MemberDto;
import com.hackday.sns_timeline.sign.domain.entity.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/timeLine")
public class TimeLineController {

	final private ProfileService profileService;
	final private ContentService contentService;


	@GetMapping
	public ModelAndView getTimeLinePage(@AuthenticationPrincipal CustomUser user, @PageableDefault Pageable pageable) throws Exception{
		Member member = profileService.getMemberProfile(user.getId());
		List<MemberDto> memberDtoList = profileService.getSubscribeMember(member);

		Page<ContentDto> contentDtoList = contentService.getMyTimelineContent(memberDtoList, pageable, user.getUsername());

		return new ModelAndView("layout/timeLine").addObject(CommonConst.CONTENT_DTO_LIST, contentDtoList);
	}
}

