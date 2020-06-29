package com.beard.terminals.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

/**
 * @author angry_beard
 */
@Getter
@Setter
@Builder
@ApiModel("响应公共类")
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {

    @ApiModelProperty(required = true, notes = "结果码", example = "200")
    private int code;
    @ApiModelProperty(required = true, notes = "返回信息", example = "SUCCESS")
    private String msg;
    @ApiModelProperty(required = true, notes = "返回数据", example = "{\"name\":\"blues\"}")
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
        return new Result(200, "success");
    }

    public static Result fail() {
        return new Result(500, "fail");
    }
}
