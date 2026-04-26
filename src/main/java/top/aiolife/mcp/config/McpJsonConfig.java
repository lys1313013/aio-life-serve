package top.aiolife.mcp.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.modelcontextprotocol.json.McpJsonMapper;
import io.modelcontextprotocol.json.McpJsonMapperSupplier;
import io.modelcontextprotocol.json.TypeRef;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * MCP JSON Mapper 配置
 * 使用Spring配置的ObjectMapper以支持Java 8日期/时间类型
 *
 * @author Lys
 * @date 2026/04/26
 */
@Component
public class McpJsonConfig implements McpJsonMapperSupplier {

    private final ObjectMapper objectMapper;

    public McpJsonConfig(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public McpJsonMapper get() {
        return new McpJsonMapper() {
            @Override
            public <T> T readValue(String content, Class<T> type) throws IOException {
                return objectMapper.readValue(content, type);
            }

            @Override
            public <T> T readValue(byte[] content, Class<T> type) throws IOException {
                return objectMapper.readValue(content, type);
            }

            @Override
            public <T> T readValue(String content, TypeRef<T> type) throws IOException {
                return objectMapper.readValue(content, objectMapper.getTypeFactory().constructType(type.getType()));
            }

            @Override
            public <T> T readValue(byte[] content, TypeRef<T> type) throws IOException {
                return objectMapper.readValue(content, objectMapper.getTypeFactory().constructType(type.getType()));
            }

            @Override
            public <T> T convertValue(Object fromValue, Class<T> type) {
                return objectMapper.convertValue(fromValue, type);
            }

            @Override
            public <T> T convertValue(Object fromValue, TypeRef<T> type) {
                return objectMapper.convertValue(fromValue, objectMapper.getTypeFactory().constructType(type.getType()));
            }

            @Override
            public String writeValueAsString(Object value) throws IOException {
                return objectMapper.writeValueAsString(value);
            }

            @Override
            public byte[] writeValueAsBytes(Object value) throws IOException {
                return objectMapper.writeValueAsBytes(value);
            }
        };
    }
}
