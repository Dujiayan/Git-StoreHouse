package cn.itcast.web.job;

import java.util.Date;

public class Job {
	public void log(){
		// 发送邮件
		System.out.println("执行了..."+new Date());
	}
}
