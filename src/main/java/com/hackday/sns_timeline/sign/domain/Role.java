package com.hackday.sns_timeline.sign.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Role {

	MEMBER("ROLE_MEMBER");

	private String value;
}