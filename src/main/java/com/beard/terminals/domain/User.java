package com.beard.terminals.domain;


import lombok.*;

/**
 * @author angry_beard
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private String name;
    private Integer age;
    private String sex;
}
