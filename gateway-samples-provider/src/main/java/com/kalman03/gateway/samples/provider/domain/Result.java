package com.kalman03.gateway.samples.provider.domain;

/**
 * @author kalman03
 * @since 2022-03-16
 */
public class Result<T> {

	private final static int SUCCESS = 0;
	private final static int ERROR = 0;

	private T data;

	private int code;

	private String message;

	public static <T> Result<T> ok(T data) {
		return new Result<T>(data, SUCCESS, null);
	}

	public static <T> Result<T> error(String message) {
		return error(ERROR, message);
	}

	public static <T> Result<T> error(int code, String message) {
		return new Result<T>(null, code, message);
	}

	public Result(T data, int code, String message) {
		this.data = data;
		this.code = code;
		this.message = message;
	}

	public T getData() {
		return data;
	}

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}
}
