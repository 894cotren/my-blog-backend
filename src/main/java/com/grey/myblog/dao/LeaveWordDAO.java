package com.grey.myblog.dao;

import com.grey.myblog.model.dataobject.LeaveWordDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 留言表 DAO
 *
 * @author grey
 */
public interface LeaveWordDAO {

    /**
     * 插入留言
     */
    int insert(LeaveWordDO leaveWord);

    /**
     * 根据ID删除留言
     */
    int deleteById(@Param("id") Long id);

    /**
     * 根据ID查询留言
     */
    LeaveWordDO selectById(@Param("id") Long id);

    /**
     * 查询全部留言列表
     */
    List<LeaveWordDO> selectAll();

    /**
     * 统计留言数
     */
    int count();
}
