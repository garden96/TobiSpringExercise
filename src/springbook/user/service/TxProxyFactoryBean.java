package springbook.user.service;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import java.lang.reflect.Proxy;

public class TxProxyFactoryBean implements FactoryBean<Object> {

    // TransactionHandler 생성시 필요한 값을 주입받기위한 프로퍼티
	Object target;
	PlatformTransactionManager transactionManager;
	String pattern;

	// 다이나믹 프록시를 생성할때 필요한 값을 주입받기위한 프로퍼티
	Class<?> serviceInterface;
	
	public void setTarget(Object target) {
		this.target = target;
	}

	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public void setServiceInterface(Class<?> serviceInterface) {
		this.serviceInterface = serviceInterface;
	}

	// FactoryBean 인터페이스 구현 메소드
	public Object getObject() throws Exception {

	    // DI 받은 정보를 이용하여 TransactionHandler를 사용하는 dynamic proxy를 생성한다.
		TransactionHandler txHandler = new TransactionHandler();
		txHandler.setTarget(target);
		txHandler.setTransactionManager(transactionManager);
		txHandler.setPattern(pattern);

		return Proxy.newProxyInstance(
			getClass().getClassLoader(),new Class[] { serviceInterface }, txHandler);
	}

	public Class<?> getObjectType() {
	    // 팩토리 빈이 생성하는 오프젝트 타입은 DI 받은 인터페이스 타입에따라 달라지며,
        // 다양한 타입의 프록시 오프젝트 생성에 재사용할 수 있다.
		return serviceInterface;
	}

	public boolean isSingleton() {
	    // false 의 의미는 싱글톤이 아니라는 의미가 아니라
        // getObject()가 매번 같은 오프젝트를 리턴하지 않는다는 의미임.
		return false;
	}
}
