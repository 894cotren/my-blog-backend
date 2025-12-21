package com.grey.myblog.controller;


import com.grey.myblog.common.Result;
import com.grey.myblog.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

/**
 * 用户接口
 *
 * @author grey
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @PostMapping("/register")
    public Result<Long> userRegister() {
        return Result.fail(ErrorCode.NOT_FOUND_ERROR,"暂未实现");
    }


    @PostMapping("/login")
    public Result<Void> userLogin() {
        return Result.fail(ErrorCode.NOT_FOUND_ERROR,"暂未实现");
    }


    @PostMapping("/update")
    public Result<Integer> updateUser() {
        return Result.fail(ErrorCode.NOT_FOUND_ERROR,"暂未实现");
    }

    @PostMapping("/delete")
    public Result<Boolean> deleteUser(@RequestBody Long id, HttpServletRequest request) {
        return Result.fail(ErrorCode.NOT_FOUND_ERROR,"暂未实现");
    }

    @PostMapping("/logout")
    public Result<Integer> userLogout(HttpServletRequest request) {
        return Result.fail(ErrorCode.NOT_FOUND_ERROR,"暂未实现");
    }

}
