package com.grey.myblog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.grey.myblog.model.entity.Tag;
import com.grey.myblog.service.TagService;
import com.grey.myblog.mapper.TagMapper;
import org.springframework.stereotype.Service;

/**
* @author grey
* @description 针对表【tag(标签表)】的数据库操作Service实现
* @createDate 2026-01-15 11:49:57
*/
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag>
    implements TagService{

}




