package com.kalman03.gateway.samples.provider.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author kalman03
 * @since 2022-03-16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {

	private long userId;
	private Object otherInfo;
}
