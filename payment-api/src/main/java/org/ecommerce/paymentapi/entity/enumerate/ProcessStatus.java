package org.ecommerce.paymentapi.entity.enumerate;

import java.util.Arrays;

import lombok.Getter;

@Getter
public enum ProcessStatus{
	PENDING("처리 전"),
	IN_PROGRESS("진행 중"),
	FAILED("실패"),
	CANCELLED("취소"),
	COMPLETED("완료");
	private final String title;

	ProcessStatus(String title) {
		this.title = title;
	}

	public static ProcessStatus getProcessStatus(String status) {
		return Arrays.stream(ProcessStatus.values()).filter((name) ->
				name.name().equals(status))
			.findFirst()
			.orElse(null);
	}

}
