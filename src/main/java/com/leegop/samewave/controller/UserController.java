package com.leegop.samewave.controller;

import com.leegop.samewave.common.context.UserContext;
import com.leegop.samewave.common.result.Result;
import com.leegop.samewave.dto.UserLoginDTO;
import com.leegop.samewave.dto.UserRegisterDTO;
import com.leegop.samewave.service.UserService;
import com.leegop.samewave.vo.LoginVO;
import com.leegop.samewave.vo.UserVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public Result<LoginVO> register(@Valid @RequestBody UserRegisterDTO dto) {
        return Result.success(userService.register(dto));
    }

    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody UserLoginDTO dto) {
        return Result.success(userService.login(dto));
    }

    @GetMapping("/{id}")
    public Result<UserVO> getById(@PathVariable Long id) {
        return Result.success(userService.getById(id));
    }

    @GetMapping("/me")
    public Result<UserVO> me() {
        return Result.success(userService.getById(UserContext.getUserId()));
    }
}
