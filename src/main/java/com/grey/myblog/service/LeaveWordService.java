package com.grey.myblog.service;

import com.grey.myblog.model.dto.LeaveWordDTO;
import com.grey.myblog.model.request.LeaveWordAddRequest;

import java.util.List;

/**
 * 留言服务接口
 *
 * @author grey
 */
public interface LeaveWordService {

    /**
     * 添加留言
     */
    Long addLeaveWord(LeaveWordAddRequest request);

    /**
     * 删除留言
     */
    Boolean deleteLeaveWord(Long id);

    /**
     * 获取全部留言列表
     */
    List<LeaveWordDTO> getAllLeaveWords();

    /**
     * 统计留言数
     */
    Integer count();
}
