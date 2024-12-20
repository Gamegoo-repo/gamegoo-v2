package com.gamegoo.gamegoo_v2.exception;

import com.gamegoo.gamegoo_v2.exception.common.ErrorCode;
import com.gamegoo.gamegoo_v2.exception.common.GlobalException;

public class BoardException extends GlobalException {

    public BoardException(ErrorCode errorCode) {
        super(errorCode);
    }

}
