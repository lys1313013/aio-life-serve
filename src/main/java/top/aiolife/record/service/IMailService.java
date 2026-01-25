package top.aiolife.record.service;

import jakarta.mail.MessagingException;

/**
 * 类功能描述
 *
 * @author Lys
 * @date 2025/04/12 00:50
 */
public interface IMailService {


    /**
     * 发送简单邮件
     *
     * @param sendTo  收件人
     * @param title   标题
     * @param content 内容
     * @author Lys
     * @date 2025/4/12 00:50
     */
    void sendSimpleEmail(String sendTo, String title, String content);

    /**
     * @param sendTo      收件人
     * @param title       标题
     * @param htmlContent html内容
     * @author Lys
     * @date 2025/5/2 20:22
     */
    void sendHtmlEmail(String sendTo, String title, String htmlContent) throws MessagingException;
}
