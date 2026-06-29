package com.grey.myblog.controller.admin;

import com.grey.myblog.annotation.AuthCheck;
import com.grey.myblog.common.Result;
import com.grey.myblog.model.dto.LeaveWordDTO;
import com.grey.myblog.model.enums.ErrorCode;
import com.grey.myblog.service.LeaveWordService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 留言接口（管理端）
 *
 * @author grey
 */
@RestController
@RequestMapping("/admin/leave-word")
public class LeaveWordAdminController {

    @Resource
    private LeaveWordService leaveWordService;

    /**
     * 获取全部留言列表
     */
    @GetMapping("/list")
    @AuthCheck
    public Result<List<LeaveWordDTO>> getAllLeaveWords() {
        List<LeaveWordDTO> leaveWords = leaveWordService.getAllLeaveWords();
        return Result.success(leaveWords);
    }

    /**
     * 删除留言
     */
    @PostMapping("/delete/{id}")
    @AuthCheck
    public Result<Boolean> deleteLeaveWord(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return Result.fail(ErrorCode.PARAMS_ERROR, "留言ID无效");
        }
        Boolean result = leaveWordService.deleteLeaveWord(id);
        return Result.success(result);
    }
}
