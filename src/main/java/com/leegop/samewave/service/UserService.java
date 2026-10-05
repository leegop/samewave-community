package com.leegop.samewave.service;

import com.leegop.samewave.dto.UserLoginDTO;
import com.leegop.samewave.dto.UserRegisterDTO;
import com.leegop.samewave.vo.LoginVO;
import com.leegop.samewave.vo.UserVO;

public interface UserService {

    LoginVO register(UserRegisterDTO dto);

    LoginVO login(UserLoginDTO dto);

    UserVO getById(Long id);
}
