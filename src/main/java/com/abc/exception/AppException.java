package com.abc.exception;

public class AppException extends RuntimeException {// đe no thanh exception va su dung phuong thuc cua lop cha
    private ErrorCode errorCode;

    public AppException(ErrorCode errorCode) {
        super(errorCode.getMessage());//ke thua controtor cua runtimeexception
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
