package top.aiolife.llm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.aiolife.llm.pojo.entity.ConversationEntity;

import java.util.List;

public interface ConversationService extends IService<ConversationEntity> {
    ConversationEntity createSession(Long userId, String title);
    List<ConversationEntity> listByUserId(Long userId);
    void deleteSession(Long userId, Long conversationId);
    void updateTitle(Long userId, Long conversationId, String title);
}
