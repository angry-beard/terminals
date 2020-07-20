package com.beard.terminals.redis.vo;

import lombok.*;

import java.io.Serializable;

/**
 * Created by angry_beard on 2020/7/20.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RedisVo implements Serializable {

    private static final long serialVersionUID = 5237730257103305078L;

    private Long id;
    private String userName;
    private String userSex;

}
