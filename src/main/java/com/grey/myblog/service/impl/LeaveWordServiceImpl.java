package com.grey.myblog.service.impl;

import cn.hutool.core.util.StrUtil;
import com.grey.myblog.dao.LeaveWordDAO;
import com.grey.myblog.exception.BusinessException;
import com.grey.myblog.model.dataobject.LeaveWordDO;
import com.grey.myblog.model.dto.LeaveWordDTO;
import com.grey.myblog.model.enums.ErrorCode;
import com.grey.myblog.model.request.LeaveWordAddRequest;
import com.grey.myblog.service.LeaveWordService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 留言服务实现类
 *
 * @author grey
 */
@Slf4j
@Service
public class LeaveWordServiceImpl implements LeaveWordService {

    @Resource
    private LeaveWordDAO leaveWordDAO;

    @Override
    public Long addLeaveWord(LeaveWordAddRequest request) {
        // 参数校验
        if (StrUtil.hasBlank(request.getNickname(), request.getContent())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "昵称和留言内容不能为空");
        }
        if (request.getNickname().length() > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "昵称长度不能超过50");
        }
        if (request.getContent().length() > 1000) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "留言内容长度不能超过1000");
        }

        LeaveWordDO leaveWord = LeaveWordDO.builder()
                .nickname(request.getNickname())
                .email(request.getEmail())
                .content(request.getContent())
                .createTime(new Date())
                .build();

        leaveWordDAO.insert(leaveWord);
        log.info("action=add_leave_word, nickname={}, result=success", request.getNickname());
        return leaveWord.getId();
    }

    @Override
    public Boolean deleteLeaveWord(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "留言ID无效");
        }
        int result = leaveWordDAO.deleteById(id);
        log.info("action=delete_leave_word, leaveWordId={}, result={}", id, result > 0 ? "success" : "fail");
        return result > 0;
    }

    @Override
    public List<LeaveWordDTO> getAllLeaveWords() {
        List<LeaveWordDO> leaveWords = leaveWordDAO.selectAll();
        return leaveWords.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Integer count() {
        return leaveWordDAO.count();
    }

    /**
     * 转换为 DTO
     */
    private LeaveWordDTO convertToDTO(LeaveWordDO leaveWordDO) {
        LeaveWordDTO dto = new LeaveWordDTO();
        BeanUtils.copyProperties(leaveWordDO, dto);
        return dto;
    }
}
