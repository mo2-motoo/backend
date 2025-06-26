package com.hsu_mafia.motoo.api.kis.exception;

import com.hsu_mafia.motoo.global.exception.BaseException;
import com.hsu_mafia.motoo.global.exception.ErrorCode;

public class KisTokenRequestException extends BaseException {
  public KisTokenRequestException() {
    super(ErrorCode.KIS_TOKEN_REQUEST_FAILED);
  }
}
