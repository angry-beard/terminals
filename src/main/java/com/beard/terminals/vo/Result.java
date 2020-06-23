package com.beard.terminals.vo;

import lombok.*;

/**
 * @author angry_beard
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {

    private int code;
    private String msg;
    private T data;

    public Result(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Result(T data) {
        this.code = 200;
        this.msg = "success";
        this.data = data;
    }

    public static Result success() {
        return new Result(null);
    }
}
