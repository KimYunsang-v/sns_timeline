package com.hackday.sns_timeline.searchMember.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hackday.sns_timeline.common.CommonFunction;
import com.hackday.sns_timeline.common.commonEnum.ATTRIBUTE;
import com.hackday.sns_timeline.searchMember.domain.dto.SearchMemberDto;
import com.hackday.sns_timeline.searchMember.domain.document.SearchMemberDoc;
import com.hackday.sns_timeline.searchMember.repository.SearchMemberEsRepository;
import com.hackday.sns_timeline.sign.domain.dto.MemberDto;
import com.hackday.sns_timeline.subscribe.domain.document.SubscribeDoc;
import com.hackday.sns_timeline.subscribe.repository.SubscribeDocRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
@RequiredArgsConstructor
public class SearchMemberService {

	final private SearchMemberEsRepository searchMemberEsRepository;
	final private SubscribeDocRepository subscribeDocRepository;

	@Transactional
	public Page<MemberDto> findMembers(String search, int page) {
		page = (page == 0) ? 0 : (page - 1);
		Pageable pageable = PageRequest.of(page, 10);
		Page<MemberDto> searchMembers = searchMemberEsRepository.findByEmailContainsOrNameContains(search, search, pageable)
			.map(MemberDto::searchMemberEsConverter);

		return searchMembers;
	}

	@Transactional
	public void checkSubscribed(Page<MemberDto> searchMembers, long id) throws Exception {
		SearchMemberDoc searchMemberDoc = searchMemberEsRepository.findByMemberId(id).orElseThrow(() -> new Exception());
		List<SubscribeDoc> subscribeList = subscribeDocRepository.findByMemberId(searchMemberDoc.getMemberId());
		Set<Long> subscribeMemberIdSet = new HashSet<>();

		subscribeList.forEach(subscribeDoc -> subscribeMemberIdSet.add(subscribeDoc.getSubscribeMemberId()));

		for (MemberDto searchMember : searchMembers) {
			if(subscribeMemberIdSet.contains(searchMember.getId())){
				searchMember.setSubscribed(true);
			}
		}
	}

	public RedirectAttributes setRedirectAttributes(RedirectAttributes redirectAttributes,
		SearchMemberDto searchMemberDto) throws Exception {

		Page<MemberDto> memberDtoList = findMembers(searchMemberDto.getSearch(), searchMemberDto.getPage());

		redirectAttributes.addFlashAttribute(ATTRIBUTE.SEARCH.getName(), searchMemberDto.getSearch());

		if(memberDtoList.getContent().size() > 0) {
			checkSubscribed(memberDtoList, searchMemberDto.getUserId());
			int start = CommonFunction.getStartPageNumber(memberDtoList.getNumber());
			int last = CommonFunction.getLastPageNumber(start, memberDtoList.getTotalPages());
			redirectAttributes.addFlashAttribute(ATTRIBUTE.START.getName(), start);
			redirectAttributes.addFlashAttribute(ATTRIBUTE.LAST.getName(), last);
			redirectAttributes.addFlashAttribute(ATTRIBUTE.MEMBER_DTO_LIST.getName(), memberDtoList);
		}else {
			redirectAttributes.addFlashAttribute(ATTRIBUTE.IS_NULL.getName(), true);
		}

		return redirectAttributes;
	}

	public Page<SearchMemberDoc> fineSearchMemberByEmailLikeOrNameLike(String email){
		return searchMemberEsRepository.findByEmailContainsOrNameContains(email, email, PageRequest.of(0, 10));
	}

	public SearchMemberDoc saveSearchMember(SearchMemberDoc memberSearch){
		return searchMemberEsRepository.save(memberSearch);
	}
}
