package com.leegop.samewave.service.impl;

import com.leegop.samewave.common.constant.UserStatus;
import com.leegop.samewave.common.exception.BusinessException;
import com.leegop.samewave.common.result.ResultCode;
import com.leegop.samewave.common.util.JwtUtil;
import com.leegop.samewave.dto.UserLoginDTO;
import com.leegop.samewave.dto.UserRegisterDTO;
import com.leegop.samewave.entity.User;
import com.leegop.samewave.mapper.UserMapper;
import com.leegop.samewave.service.UserService;
import com.leegop.samewave.vo.LoginVO;
import com.leegop.samewave.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public LoginVO register(UserRegisterDTO dto) {
        if (userMapper.selectByUsername(dto.getUsername()) != null) {
            throw new BusinessException("用户名已被占用");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setNickname(dto.getNickname());

        try {
            userMapper.insert(user);
        } catch (DuplicateKeyException e) {
            throw new BusinessException("用户名已被占用");
        }

        return issueToken(user);
    }

    @Override
    public LoginVO login(UserLoginDTO dto) {
        User user = userMapper.selectByUsername(dto.getUsername());

        if (user == null || !passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        UserStatus status = UserStatus.of(user.getStatus());
        if (status != UserStatus.NORMAL) {
            String msg = (status == null ? "账号状态异常" : "账号" + status.getDesc());
            throw new BusinessException(msg + "，请联系管理员");
        }

        userMapper.updateLastLoginTime(user.getId());

        return issueToken(user);
    }

    @Override
    public UserVO getById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    private LoginVO issueToken(User user) {
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return new LoginVO(token, userVO);
    }
}
