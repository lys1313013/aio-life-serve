package top.aiolife.llm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.aiolife.llm.pojo.entity.ChatMessageEntity;

import java.util.List;

public interface ChatMessageService extends IService<ChatMessageEntity> {

    List<ChatMessageEntity> listByUserId(Long userId);

    ChatMessageEntity saveMessage(Long userId, String role, String content, String modelName);

    void deleteByUserId(Long userId);
}
