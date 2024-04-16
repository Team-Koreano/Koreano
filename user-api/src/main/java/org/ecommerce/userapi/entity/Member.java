package org.ecommerce.userapi.entity;

import org.ecommerce.userapi.entity.type.UserStatus;

public interface Member {
	Integer getId();
	String getEmail();
	UserStatus getUserStatus();
}
