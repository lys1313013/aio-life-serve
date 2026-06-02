package top.aiolife.wardrobe.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.aiolife.wardrobe.mapper.WardrobeCategoryMapper;
import top.aiolife.wardrobe.mapper.WardrobeItemMapper;
import top.aiolife.wardrobe.pojo.entity.WardrobeCategoryEntity;
import top.aiolife.wardrobe.pojo.entity.WardrobeItemEntity;
import top.aiolife.wardrobe.pojo.req.WardrobeItemReq;
import top.aiolife.wardrobe.pojo.vo.WardrobeItemVO;
import top.aiolife.wardrobe.pojo.vo.WardrobeStatsVO;
import top.aiolife.wardrobe.service.IWardrobeItemService;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 衣柜衣物 Service 实现
 */
@Slf4j
@Service
@AllArgsConstructor
public class WardrobeItemServiceImpl extends ServiceImpl<WardrobeItemMapper, WardrobeItemEntity> implements IWardrobeItemService {

    private final WardrobeCategoryMapper categoryMapper;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveItem(WardrobeItemReq req) {
        WardrobeItemEntity entity = reqToEntity(req);
        Long userId = StpUtil.getLoginIdAsLong();
        entity.setUserId(userId);
        entity.fillCreateCommonField(userId);
        entity.setSeason(joinSeason(req.getSeason()));
        entity.setPhotoUrls(toJson(req.getPhotoUrls()));
        this.save(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateItem(WardrobeItemReq req) {
        WardrobeItemEntity entity = reqToEntity(req);
        entity.setId(req.getId());
        Long userId = StpUtil.getLoginIdAsLong();
        entity.fillUpdateCommonField(userId);
        entity.setSeason(joinSeason(req.getSeason()));
        entity.setPhotoUrls(toJson(req.getPhotoUrls()));
        this.updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeItem(Long id, Long userId) {
        LambdaQueryWrapper<WardrobeItemEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WardrobeItemEntity::getId, id);
        queryWrapper.eq(WardrobeItemEntity::getUserId, userId);
        this.remove(queryWrapper);
    }

    @Override
    public WardrobeItemVO getItem(Long id, Long userId) {
        WardrobeItemEntity entity = this.lambdaQuery()
                .eq(WardrobeItemEntity::getId, id)
                .eq(WardrobeItemEntity::getUserId, userId)
                .one();
        if (entity == null) {
            return null;
        }
        return toVO(entity);
    }

    @Override
    public List<WardrobeItemVO> listItems(Long userId, Long categoryId, String season, String keyword) {
        LambdaQueryWrapper<WardrobeItemEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WardrobeItemEntity::getUserId, userId);

        if (categoryId != null) {
            queryWrapper.eq(WardrobeItemEntity::getCategoryId, categoryId);
        }
        if (season != null && !season.isEmpty()) {
            queryWrapper.like(WardrobeItemEntity::getSeason, season);
        }
        if (keyword != null && !keyword.isEmpty()) {
            queryWrapper.and(w -> w.like(WardrobeItemEntity::getName, keyword)
                    .or().like(WardrobeItemEntity::getColor, keyword)
                    .or().like(WardrobeItemEntity::getBrand, keyword));
        }

        queryWrapper.orderByDesc(WardrobeItemEntity::getCreateTime);
        List<WardrobeItemEntity> list = this.list(queryWrapper);
        return list.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public WardrobeStatsVO getStats(Long userId) {
        List<WardrobeItemEntity> items = this.lambdaQuery()
                .eq(WardrobeItemEntity::getUserId, userId)
                .list();

        WardrobeStatsVO stats = new WardrobeStatsVO();
        stats.setTotalCount((long) items.size());

        // 分类统计
        Map<String, Long> categoryCount = new HashMap<>();
        Map<Long, String> categoryNameMap = getCategoryNameMap();
        for (WardrobeItemEntity item : items) {
            String name = categoryNameMap.getOrDefault(item.getCategoryId(), "未分类");
            categoryCount.put(name, categoryCount.getOrDefault(name, 0L) + 1);
        }
        stats.setCategoryCount(categoryCount);

        // 季节统计
        Map<String, Long> seasonCount = new HashMap<>();
        seasonCount.put("春", 0L);
        seasonCount.put("夏", 0L);
        seasonCount.put("秋", 0L);
        seasonCount.put("冬", 0L);
        for (WardrobeItemEntity item : items) {
            if (item.getSeason() != null) {
                if (item.getSeason().contains("春")) seasonCount.put("春", seasonCount.get("春") + 1);
                if (item.getSeason().contains("夏")) seasonCount.put("夏", seasonCount.get("夏") + 1);
                if (item.getSeason().contains("秋")) seasonCount.put("秋", seasonCount.get("秋") + 1);
                if (item.getSeason().contains("冬")) seasonCount.put("冬", seasonCount.get("冬") + 1);
            }
        }
        stats.setSeasonCount(seasonCount);

        // 价格统计
        BigDecimal totalValue = BigDecimal.ZERO;
        for (WardrobeItemEntity item : items) {
            if (item.getPrice() != null) {
                totalValue = totalValue.add(item.getPrice());
            }
        }
        stats.setTotalValue(totalValue);
        if (!items.isEmpty()) {
            stats.setAvgPrice(totalValue.divide(BigDecimal.valueOf(items.size()), 2, BigDecimal.ROUND_HALF_UP));
        } else {
            stats.setAvgPrice(BigDecimal.ZERO);
        }

        return stats;
    }

    private WardrobeItemEntity reqToEntity(WardrobeItemReq req) {
        WardrobeItemEntity entity = new WardrobeItemEntity();
        entity.setName(req.getName());
        entity.setCategoryId(req.getCategoryId());
        entity.setColor(req.getColor());
        entity.setBrand(req.getBrand());
        entity.setPurchaseDate(req.getPurchaseDate());
        entity.setPrice(req.getPrice());
        entity.setSize(req.getSize());
        entity.setMemo(req.getMemo());
        return entity;
    }

    private WardrobeItemVO toVO(WardrobeItemEntity entity) {
        WardrobeItemVO vo = new WardrobeItemVO();
        vo.setId(entity.getId());
        vo.setName(entity.getName());
        vo.setCategoryId(entity.getCategoryId());
        vo.setCategoryName(getCategoryNameMap().get(entity.getCategoryId()));
        vo.setColor(entity.getColor());
        vo.setBrand(entity.getBrand());
        vo.setSeason(entity.getSeason());
        vo.setPurchaseDate(entity.getPurchaseDate());
        vo.setPrice(entity.getPrice());
        vo.setPhotoUrls(fromJson(entity.getPhotoUrls()));
        vo.setSize(entity.getSize());
        vo.setMemo(entity.getMemo());
        vo.setCreateTime(entity.getCreateTime());
        return vo;
    }

    private Map<Long, String> getCategoryNameMap() {
        List<WardrobeCategoryEntity> categories = categoryMapper.selectList(null);
        return categories.stream()
                .collect(Collectors.toMap(WardrobeCategoryEntity::getId, WardrobeCategoryEntity::getName, (a, b) -> a));
    }

    private String joinSeason(List<String> season) {
        if (season == null || season.isEmpty()) {
            return null;
        }
        return String.join(",", season);
    }

    private String toJson(List<String> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            log.error("JSON序列化失败", e);
            return null;
        }
    }

    private List<String> fromJson(String json) {
        if (json == null || json.isEmpty()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            log.error("JSON反序列化失败", e);
            return Collections.emptyList();
        }
    }
}
