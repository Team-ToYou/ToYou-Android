package com.toyou.core.domain.model

enum class StatusType(val value: String) {
    SCHOOL("SCHOOL"),
    COLLEGE("COLLEGE"),
    OFFICE("OFFICE"),
    ETC("ETC")
}

enum class DuplicateCheckMessageType(val message: String) {
    CHECK_REQUIRED("중복된 닉네임인지 확인해주세요"),
    LENGTH_EXCEEDED("15자 이내로 입력해주세요."),
    AVAILABLE("사용 가능한 닉네임입니다."),
    ALREADY_IN_USE("이미 사용 중인 닉네임입니다."),
    ALREADY_IN_USE_SAME("이미 사용 중인 닉네임입니다."),
    CHECK_FAILED("닉네임 확인에 실패했습니다."),
    UPDATE_FAILED("닉네임 변경에 실패했습니다."),
    SERVER_ERROR("서버에 연결할 수 없습니다.")
}
