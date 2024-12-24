package com.gamegoo.gamegoo_v2.exception;

import com.gamegoo.gamegoo_v2.exception.common.ErrorCode;
import com.gamegoo.gamegoo_v2.exception.common.GlobalException;

public class SocketException extends GlobalException {

    public SocketException(ErrorCode errorCode) {
        super(errorCode);
    }

}
