package top.aiolife.mcp.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * MCP 字段描述注解
 *
 * @author Lys
 * @date 2026/04/26
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface McpField {

    /**
     * 字段描述
     */
    String description() default "";
}
