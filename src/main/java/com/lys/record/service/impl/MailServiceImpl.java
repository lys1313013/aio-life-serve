package com.lys.record.service.impl;

import com.lys.record.service.IMailService;
import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * 类功能描述
 *
 * @author Lys
 * @date 2025/04/12 00:50
 */
@Slf4j
@Service
public class MailServiceImpl implements IMailService {

    /**
     * 发送邮件的邮箱账号
     */
    @Value("${spring.mail.username}")
    private String sendFrom;

    @Resource
    private JavaMailSenderImpl mailSender;

    @Override
    public void sendSimpleEmail(String sendTo, String title, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(sendFrom);  // 使用配置的sendFrom字段
        message.setTo(sendTo);
        message.setSubject(title);
        message.setText(content);
        mailSender.send(message);
        log.info("邮件发送成功，收件人：{}， 标题：{}， 内容：{}", sendTo, title, content);
    }


    @Override
    public void sendHtmlEmail(String sendTo, String title, String htmlContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(sendFrom);
        helper.setTo(sendTo);
        helper.setSubject(title);
        helper.setText(htmlContent, true);
        mailSender.send(message);
        log.info("HTML邮件发送成功，收件人：{}， 标题：{}", sendTo, title);
    }
}
