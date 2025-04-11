package com.lys.record.service;

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
}
