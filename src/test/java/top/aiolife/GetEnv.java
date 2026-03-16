package top.aiolife;

/**
 * 读取环境变量的测试程序
 * 主要用于读取 AIO_LIFE_DB_URL 环境变量
 */
public class GetEnv {
    public static void main(String[] args) {
        // 读取并打印 AIO_LIFE_DB_URL 环境变量
        String dbUrl = System.getenv("AIO_LIFE_DB_URL");
        System.out.println("AIO_LIFE_DB_URL: " + (dbUrl != null ? dbUrl : "环境变量未设置"));

        // 读取其他相关环境变量（可选）
        String dbPassword = System.getenv("AIO_LIFE_DB_PASSWORD");
        System.out.println("AIO_LIFE_DB_PASSWORD: " + (dbPassword != null ? dbPassword : "环境变量未设置"));

        String serverIp = System.getenv("AIO_LIFE_SERVER_IP");
        System.out.println("AIO_LIFE_SERVER_IP: " + (serverIp != null ? serverIp : "环境变量未设置"));

        String serverUser = System.getenv("AIO_LIFE_SERVER_USER");
        System.out.println("AIO_LIFE_SERVER_USER: " + (serverUser != null ? serverUser : "环境变量未设置"));

        String serverPassword = System.getenv("AIO_LIFE_SERVER_PASSWORD");
        System.out.println("AIO_LIFE_SERVER_PASSWORD: " + (serverPassword != null ? serverPassword : "环境变量未设置"));
    }
}
