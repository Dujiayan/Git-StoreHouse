package cn.itcast.test;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cn.itcast.dao.DeptDao;
import cn.itcast.domain.Dept;
import cn.itcast.service.DeptService;

@RunWith(value=SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class Demo1 {
	@Autowired
	private DeptService deptService;
	/*@Autowired
	private DeptDao deptDao;*/
	
	/**
	 * 入门程序
	 */
	@Test
	public void run1(){
		Dept dept = deptService.get("100");
		System.out.println(dept.getDeptName());
	}

	public void run2(){
		//条件查询
		Specification<Dept> spec = new Specification<Dept>() {
			//生成查询条件的
			//QBC:c.add(Restrictions.eq("属性名称","值"))
			@Override
			public Predicate toPredicate(Root<Dept> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				//root获取属性
				//cb生成eq查询
				Predicate p = cb.equal(root.get("state").as(Integer.class),1);
				return p;
			}
		};
	}
}
