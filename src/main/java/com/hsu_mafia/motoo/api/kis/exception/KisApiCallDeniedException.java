package com.hsu_mafia.motoo.api.kis.exception;

import com.hsu_mafia.motoo.global.exception.BaseException;
import com.hsu_mafia.motoo.global.exception.ErrorCode;

public class KisApiCallDeniedException extends BaseException {
    public KisApiCallDeniedException() {
        super(ErrorCode.KIS_API_CALL_DENIED);
    }
}
