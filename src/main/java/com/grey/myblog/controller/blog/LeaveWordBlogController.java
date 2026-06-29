package com.grey.myblog.controller.blog;

import com.grey.myblog.common.Result;
import com.grey.myblog.model.dto.LeaveWordDTO;
import com.grey.myblog.model.request.LeaveWordAddRequest;
import com.grey.myblog.service.LeaveWordService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 留言接口（博客端）
 *
 * @author grey
 */
@RestController
@RequestMapping("/blog/leave-word")
public class LeaveWordBlogController {

    @Resource
    private LeaveWordService leaveWordService;

    /**
     * 添加留言
     */
    @PostMapping("/add")
    public Result<Long> addLeaveWord(@RequestBody LeaveWordAddRequest request) {
        Long leaveWordId = leaveWordService.addLeaveWord(request);
        return Result.success(leaveWordId);
    }

    /**
     * 获取全部留言列表
     */
    @GetMapping("/list")
    public Result<List<LeaveWordDTO>> getAllLeaveWords() {
        List<LeaveWordDTO> leaveWords = leaveWordService.getAllLeaveWords();
        return Result.success(leaveWords);
    }

    /**
     * 统计留言数
     */
    @GetMapping("/count")
    public Result<Integer> count() {
        Integer count = leaveWordService.count();
        return Result.success(count);
    }
}
