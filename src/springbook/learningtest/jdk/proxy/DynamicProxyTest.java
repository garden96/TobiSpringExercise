package springbook.learningtest.jdk.proxy;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.junit.Test;


public class DynamicProxyTest {
	@Test
    // client
	public void simpleProxy() {
	    //target class test
		Hello hello = new HelloTarget();
		assertThat(hello.sayHello("Toby"), is("Hello Toby"));
		assertThat(hello.sayHi("Toby"), is("Hi Toby"));
		assertThat(hello.sayThankYou("Toby"), is("Thank You Toby"));

		Hello proxiedHello = (Hello)Proxy.newProxyInstance(
				getClass().getClassLoader(),                // 동적으로 생성되는 다이나믹 프록시 클래스의 로딩에 사용할 클래스 로더
				new Class[] { Hello.class},                 // 구현할 인터페이스
				new UppercaseHandler(new HelloTarget()));   // 부가기능과 위임 코드를 담은 invacationHandler

		assertThat(proxiedHello.sayHello("Toby"), is("HELLO TOBY"));
		assertThat(proxiedHello.sayHi("Toby"), is("HI TOBY"));
		assertThat(proxiedHello.sayThankYou("Toby"), is("THANK YOU TOBY"));
	}

	// dynamic proxy class
	static class UppercaseHandler implements InvocationHandler {
		Hello target;      // object for injection

		private UppercaseHandler(Hello target) {
			this.target = target;
		}

		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {
		    String ret = (String)method.invoke(target, args);    // 타겟으로 위임. 인터페이스의 메소드 호출에 모두 적용됨.
			return ret.toUpperCase();                            // 부가기능 제공.
		}
	}

    // interface for the target class
    static interface Hello {
		String sayHello(String name);
		String sayHi(String name);
		String sayThankYou(String name);
	}

	// target class
	static class HelloTarget implements Hello {
		public String sayHello(String name) {
			return "Hello " + name;
		}

		public String sayHi(String name) {
			return "Hi " + name;
		}

		public String sayThankYou(String name) {
			return "Thank You " + name;
		}
	}
}
