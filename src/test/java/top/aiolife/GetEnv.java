package top.aiolife;

public class GetEnv {
    public static void main(String[] args) {
        System.out.println(System.getenv("AIO_LIFE_DB_PASSWORD"));
        System.out.println(System.getenv("AIO_LIFE_DB_URL"));
        System.out.println(System.getenv("AIO_LIFE_SERVER_IP"));
        System.out.println(System.getenv("AIO_LIFE_SERVER_USER"));
        System.out.println(System.getenv("AIO_LIFE_SERVER_PASSWORD"));
    }
}
