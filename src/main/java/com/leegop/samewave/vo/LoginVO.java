package com.leegop.samewave.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginVO {

    private String token;

    /** token 有效期，单位毫秒，前端据此安排续期 */
    private Long expiresIn;

    private UserSelfVO user;
}