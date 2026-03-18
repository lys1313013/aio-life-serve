package top.aiolife.llm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.aiolife.llm.mapper.ChatMessageMapper;
import top.aiolife.llm.pojo.entity.ChatMessageEntity;
import top.aiolife.llm.service.ChatMessageService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageServiceImpl extends ServiceImpl<ChatMessageMapper, ChatMessageEntity> implements ChatMessageService {

    @Override
    public List<ChatMessageEntity> listByUserId(Long userId) {
        return this.list(new LambdaQueryWrapper<ChatMessageEntity>()
                .eq(ChatMessageEntity::getUserId, userId)
                .orderByAsc(ChatMessageEntity::getCreateTime));
    }

    @Override
    public List<ChatMessageEntity> listByconversationId(Long userId, Long conversationId) {
        return this.list(new LambdaQueryWrapper<ChatMessageEntity>()
                .eq(ChatMessageEntity::getUserId, userId)
                .eq(ChatMessageEntity::getConversationId, conversationId)
                .orderByAsc(ChatMessageEntity::getCreateTime));
    }

    @Override
    public ChatMessageEntity saveMessage(Long userId, String role, String content, String modelName) {
        return saveMessage(userId, null, role, content, modelName);
    }

    @Override
    public ChatMessageEntity saveMessage(Long userId, Long conversationId, String role, String content, String modelName) {
        ChatMessageEntity message = new ChatMessageEntity();
        message.setUserId(userId);
        message.setConversationId(conversationId);
        message.setRole(role);
        message.setContent(content);
        message.setModelName(modelName);
        message.fillCreateCommonField(userId);
        this.save(message);
        return message;
    }

    @Override
    public void deleteByUserId(Long userId) {
        this.remove(new LambdaQueryWrapper<ChatMessageEntity>()
                .eq(ChatMessageEntity::getUserId, userId));
    }

    @Override
    public void deleteByconversationId(Long userId, Long conversationId) {
        this.remove(new LambdaQueryWrapper<ChatMessageEntity>()
                .eq(ChatMessageEntity::getUserId, userId)
                .eq(ChatMessageEntity::getConversationId, conversationId));
    }
}
