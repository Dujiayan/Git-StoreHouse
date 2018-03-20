package cn.itcast.mail.test;

import java.io.File;

import javax.mail.internet.MimeMessage;

import org.apache.xbean.spring.context.ClassPathXmlApplicationContext;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

public class Demo1 {
	@Test
	public void run2() throws Exception {
	 //加载配置文件,获取到邮箱对象
		ApplicationContext ac = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-mail.xml");
		//获取到邮箱发送对象
		JavaMailSenderImpl  s = (JavaMailSenderImpl) ac.getBean("javaMailSenderImpl");
		//需要创建复杂邮件
		MimeMessage message = s.createMimeMessage();
		//Spring提供帮助类,为了简化开发
		MimeMessageHelper helper = new MimeMessageHelper(message,true);
		//设置收件人
		helper.setFrom("aaa@ee298.com");
		
		helper.setTo("bbb@ee298.com");
		helper.setSubject("福利内容");
		
		helper.setText("<html><head></head><body><h1>hello!!spring image html mail</h1><a href=http://www.baidu.com>baidu</a><img src='cid:image' /></body></html>", true);
		//需要给图片真正的地址
		helper.addAttachment("image", new File("D:\\软件\\迅雷\\安装包\\弱气乙女合集(323P+9V)\\图片\\楼道\\IMG_1475.jpg"));
		//设置附件
		
		//发送
		s.send(message);
}
}
