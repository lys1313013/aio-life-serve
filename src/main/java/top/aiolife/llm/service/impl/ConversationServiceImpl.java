package top.aiolife.llm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import top.aiolife.llm.mapper.ChatSessionMapper;
import top.aiolife.llm.pojo.entity.ConversationEntity;
import top.aiolife.llm.service.ChatMessageService;
import top.aiolife.llm.service.ConversationService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class ConversationServiceImpl extends ServiceImpl<ChatSessionMapper, ConversationEntity> implements ConversationService {

    private final ChatMessageService chatMessageService;

    @Override
    public ConversationEntity createSession(Long userId, String title) {
        ConversationEntity session = new ConversationEntity();
        session.fillCreateCommonField(userId);
        session.setTitle(title);
        this.save(session);
        return session;
    }

    @Override
    public List<ConversationEntity> listByUserId(Long userId) {
        return this.list(new LambdaQueryWrapper<ConversationEntity>()
                .eq(ConversationEntity::getUserId, userId)
                .orderByDesc(ConversationEntity::getUpdateTime));
    }

    @Override
    public void deleteSession(Long userId, Long conversationId) {
        this.remove(new LambdaQueryWrapper<ConversationEntity>()
                .eq(ConversationEntity::getUserId, userId)
                .eq(ConversationEntity::getId, conversationId));
        // Also delete messages in this session
        chatMessageService.deleteByconversationId(userId, conversationId);
    }

    @Override
    public void updateTitle(Long userId, Long conversationId, String title) {
        ConversationEntity session = this.getOne(new LambdaQueryWrapper<ConversationEntity>()
                .eq(ConversationEntity::getUserId, userId)
                .eq(ConversationEntity::getId, conversationId));
        if (session != null) {
            session.setTitle(title);
            session.setUpdateTime(LocalDateTime.now());
            session.setUpdateUser(userId);
            this.updateById(session);
        }
    }
}
