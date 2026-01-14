package com.grey.myblog.controller;


import cn.hutool.core.util.ObjUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.grey.myblog.annotation.AuthCheck;
import com.grey.myblog.common.Result;
import com.grey.myblog.constant.UserConstant;
import com.grey.myblog.exception.BusinessException;
import com.grey.myblog.exception.ThrowUtil;
import com.grey.myblog.model.DeleteRequest;
import com.grey.myblog.model.entity.User;
import com.grey.myblog.model.enums.ErrorCode;
import com.grey.myblog.model.request.*;
import com.grey.myblog.model.vo.LoginUserVO;
import com.grey.myblog.model.vo.UserVO;
import com.grey.myblog.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 用户接口
 *
 * @author grey
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (ObjectUtils.isEmpty(userRegisterRequest)) {
            return Result.fail(ErrorCode.PARAMS_ERROR, "注册体为空");
        }
        String userAccount = userRegisterRequest.getAccount();
        String userPassword = userRegisterRequest.getPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        long userId = userService.userRegister(userAccount, userPassword, checkPassword);
        return Result.success(userId);
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<LoginUserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (ObjectUtils.isEmpty(userLoginRequest) || ObjectUtils.isEmpty(request) ) {
            return Result.fail(ErrorCode.PARAMS_ERROR, "登录请求体为空");
        }
        String userAccount = userLoginRequest.getAccount();
        String userPassword = userLoginRequest.getPassword();
        LoginUserVO loginUserVo = userService.userLogin(userAccount, userPassword, request);
        return Result.success(loginUserVo);
    }

    /**
     * 获取当前用户
     */
    @GetMapping("/getLoginUser")
    public Result<LoginUserVO> getLoginUser(HttpServletRequest request) {
        if (request==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        User loginUser = userService.getLoginUser(request);
        return Result.success(userService.getLoginUserVo(loginUser));
    }

    /**
     * 用户登出
     */
    @GetMapping("/logout")
    public Result<Boolean> userLogout(HttpServletRequest request){
        if (request==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = userService.userLogout(request);
        return Result.success(result);
    }


    /**
     * 用户添加
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN)
    public Result<Boolean> userAdd(@RequestBody UserAddRequest userAddRequest) {
        if (ObjectUtils.isEmpty(userAddRequest)) {
            return Result.fail(ErrorCode.PARAMS_ERROR);
        }
        boolean result = userService.userAdd(userAddRequest);
        return Result.success(result);
    }

    /**
     * 用户删除
     */
    @PostMapping("/delete")
    public Result<Boolean> userDelete(@RequestBody DeleteRequest deleteRequest) {
        /**
         * 入参：用户id，封装一个deleterequest请求体来获取id吧
         * 1. 校验用户id有效性
         * 2. 进行删除
         * 出参：boolean是否删除成功
         */
        //校验用户id有效性
        if (ObjectUtils.isEmpty(deleteRequest)) {
            return Result.fail(ErrorCode.PARAMS_ERROR);
        }
        if (deleteRequest.getId() <= 0L) {
            return Result.fail(ErrorCode.PARAMS_ERROR);
        }
        //进行删除
        boolean result = userService.removeById(deleteRequest.getId());
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除用户失败");
        }
        return Result.success(result);
    }

    /**
     * 用户更新
     */
    @PostMapping("/update")
    public Result<Boolean> userUpdate(@RequestBody UserUpdateRequest userUpdateRequest,
                                            HttpServletRequest request) {

        //校验非空
        if (ObjectUtils.isEmpty(userUpdateRequest)) {
            return Result.fail(ErrorCode.PARAMS_ERROR);
        }
        //获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        ThrowUtil.throwIf(ObjUtil.isEmpty(loginUser),ErrorCode.NOT_LOGIN_ERROR,"当前未登录");


        //进行更新
        boolean result = userService.updateUser(userUpdateRequest,loginUser);
        return Result.success(result);
    }

    /**
     * 分页查询用户
     */
    @PostMapping("/pageList")
    @AuthCheck(mustRole = UserConstant.ADMIN)
    public Result<Page<UserVO>> userPageList(@RequestBody UserPageListRequest userPageListRequest) {

        //校验非空
        if (ObjectUtils.isEmpty(userPageListRequest)) {
            return Result.fail(ErrorCode.PARAMS_ERROR);
        }
        //进行分页查询
        Page<UserVO> userVoPage = userService.userPageList(userPageListRequest);
        return Result.success(userVoPage);
    }


    /**
     * 根据用户ID查询用户（注意配置管理员权限）
     */
    @GetMapping("/getUserById")
    public Result<UserVO> getUserVoById(long id) {
        //参数校验
        if (id<=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"非法id");
        }
        //进行查询
        User user = userService.getById(id);
        //非空判断，如果为空，抛出无数据报错
        if(user==null){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return Result.success(userService.getUserVo(user));
    }

    /**
     * 获取当前用户信息（用于编辑）
     */
    @GetMapping("/getCurrentUser")
    public Result<UserVO> getCurrentUser(HttpServletRequest request) {
        //获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        ThrowUtil.throwIf(ObjUtil.isEmpty(loginUser),ErrorCode.NOT_LOGIN_ERROR,"当前未登录");
        return Result.success(userService.getUserVo(loginUser));
    }



}
