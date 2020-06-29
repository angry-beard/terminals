package com.beard.terminals.domain;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

/**
 * @author angry_beard
 */
@Getter
@Setter
@Builder
@ApiModel("用户对象")
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @ApiModelProperty(required = true, notes = "用户名", example = "哪吒")
    private String name;
    @ApiModelProperty(notes = "年龄", example = "22")
    private Integer age;
    @ApiModelProperty(notes = "性别", example = "女")
    private String sex;
}
