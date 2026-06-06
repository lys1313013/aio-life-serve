package top.aiolife.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import top.aiolife.system.pojo.entity.UserQuickNavEntity;

/**
 * 用户快捷导航 Mapper 接口
 *
 * @author Ethan
 * @date 2026/06/05
 */
public interface IUserQuickNavMapper extends BaseMapper<UserQuickNavEntity> {

    /**
     * 物理删除某用户的全部快捷导航（绕过 @TableLogic 逻辑删除）。
     *
     * <p>整块保存时需要物理清空，否则遗留的逻辑删除行会与唯一索引 (user_id, menu_id) 冲突。</p>
     */
    @Delete("DELETE FROM user_quick_nav WHERE user_id = #{userId}")
    int deleteAllByUserIdPhysical(@Param("userId") Long userId);
}
