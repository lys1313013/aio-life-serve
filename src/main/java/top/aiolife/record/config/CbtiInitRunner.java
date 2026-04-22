package top.aiolife.record.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import top.aiolife.config.CbtiConfig;
import top.aiolife.config.MinioConfig;
import top.aiolife.core.util.MinioUtil;
import top.aiolife.record.mapper.ICbtiPersonalityMapper;
import top.aiolife.record.pojo.entity.CbtiPersonalityEntity;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * CBTI 初始化任务
 *
 * @author Ethan
 * @date 2026/04/18
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CbtiInitRunner implements CommandLineRunner {

    private static final long SYSTEM_USER_ID = 0L;

    private final CbtiConfig cbtiConfig;

    private final MinioConfig minioConfig;

    private final MinioUtil minioUtil;

    private final ICbtiPersonalityMapper cbtiPersonalityMapper;

    private final ObjectMapper objectMapper;

    @Override
    public void run(String... args) throws Exception {
        if (!cbtiConfig.isEnabled() || !cbtiConfig.isInitOnStartup()) {
            return;
        }
        if (!StringUtils.hasText(cbtiConfig.getPersonalitiesPath())) {
            log.warn("CBTI 初始化跳过：未配置 aio.life.serve.cbti.personalities-path");
            return;
        }

        List<Map<String, Object>> list = loadPersonalities(cbtiConfig.getPersonalitiesPath());
        if (list.isEmpty()) {
            log.warn("CBTI 初始化跳过：未读取到人格数据");
            return;
        }

        String bucketName = resolveBucketName();
        String prefix = normalizePrefix(cbtiConfig.getObjectPrefix());

        for (Map<String, Object> item : list) {
            String code = Objects.toString(item.get("code"), null);
            if (!StringUtils.hasText(code)) {
                continue;
            }
            CbtiPersonalityEntity entity = upsertPersonality(item, prefix);
            uploadCharacterIfPresent(bucketName, entity.getImageObject());
        }

        log.info("CBTI 初始化完成：共处理 {} 条人格数据", list.size());
    }

    private List<Map<String, Object>> loadPersonalities(String path) throws Exception {
        String content = Files.readString(Path.of(path));
        return objectMapper.readValue(content, new TypeReference<List<Map<String, Object>>>() {
        });
    }

    private CbtiPersonalityEntity upsertPersonality(Map<String, Object> item, String prefix) throws Exception {
        String code = Objects.toString(item.get("code"), "");

        LambdaQueryWrapper<CbtiPersonalityEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CbtiPersonalityEntity::getIsDeleted, 0);
        wrapper.eq(CbtiPersonalityEntity::getCode, code);
        CbtiPersonalityEntity exist = cbtiPersonalityMapper.selectOne(wrapper);

        String imageName = Objects.toString(item.get("image"), "");
        String imageObject = StringUtils.hasText(imageName) ? prefix + imageName : null;

        Integer isSpecial = Boolean.TRUE.equals(item.get("isSpecial")) ? 1 : 0;

        CbtiPersonalityEntity entity = exist != null ? exist : new CbtiPersonalityEntity();
        entity.setCode(code);
        entity.setName(Objects.toString(item.get("name"), null));
        entity.setMotto(Objects.toString(item.get("motto"), null));
        entity.setColor(Objects.toString(item.get("color"), null));
        entity.setDescription(Objects.toString(item.get("description"), null));
        entity.setTechStack(Objects.toString(item.get("techStack"), null));
        entity.setSpirit(Objects.toString(item.get("spirit"), null));
        entity.setImageObject(imageObject);
        entity.setIsSpecial(isSpecial);

        entity.setVector(objectMapper.writeValueAsString(item.get("vector")));
        entity.setStrengths(objectMapper.writeValueAsString(item.get("strengths")));
        entity.setWeaknesses(objectMapper.writeValueAsString(item.get("weaknesses")));

        if (exist == null) {
            entity.fillCreateCommonField(SYSTEM_USER_ID);
            cbtiPersonalityMapper.insert(entity);
            return entity;
        }

        entity.fillUpdateCommonField(SYSTEM_USER_ID);
        cbtiPersonalityMapper.updateById(entity);
        return entity;
    }

    private void uploadCharacterIfPresent(String bucketName, String imageObject) {
        if (!StringUtils.hasText(bucketName) || !StringUtils.hasText(imageObject)) {
            return;
        }
        if (!StringUtils.hasText(cbtiConfig.getCharactersDir())) {
            return;
        }

        Path dir = Path.of(cbtiConfig.getCharactersDir());
        Path filePath = dir.resolve(Path.of(imageObject).getFileName().toString());
        if (!Files.exists(filePath)) {
            return;
        }
        if (minioUtil.objectExists(bucketName, imageObject)) {
            return;
        }

        try (InputStream inputStream = Files.newInputStream(filePath)) {
            long size = Files.size(filePath);
            minioUtil.putObject(bucketName, imageObject, inputStream, size, "image/png");
            log.info("CBTI 图片上传成功: {}/{}", bucketName, imageObject);
        } catch (Exception e) {
            log.warn("CBTI 图片上传失败: bucket={}, object={}", bucketName, imageObject, e);
        }
    }

    private String resolveBucketName() {
        if (StringUtils.hasText(cbtiConfig.getBucketName())) {
            return cbtiConfig.getBucketName();
        }
        if (minioConfig != null && StringUtils.hasText(minioConfig.getBucketName())) {
            return minioConfig.getBucketName();
        }
        return "aiolife";
    }

    private String normalizePrefix(String prefix) {
        String p = StringUtils.hasText(prefix) ? prefix : "images/cbti/characters/";
        p = p.startsWith("/") ? p.substring(1) : p;
        return p.endsWith("/") ? p : p + "/";
    }
}

