package com.hsu_mafia.motoo.api.kis.exception;

import com.hsu_mafia.motoo.global.exception.BaseException;
import com.hsu_mafia.motoo.global.exception.ErrorCode;

public class KisApiCallTooManyException extends BaseException {
    public KisApiCallTooManyException() {super(ErrorCode.KIS_API_CALL_TOO_MANY);}
}
