package springbook.learningtest.jdk.proxy;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.Test;
import org.springframework.aop.framework.ProxyFactoryBean;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


public class DynamicProxyTest {

    @Test
    // client using JDK dynamic proxy
    public void simpleProxy() {
        //target class test
        Hello hello = new HelloTarget();
        assertThat(hello.sayHello("Toby"), is("Hello Toby"));
        assertThat(hello.sayHi("Toby"), is("Hi Toby"));
        assertThat(hello.sayThankYou("Toby"), is("Thank You Toby"));

        Hello proxiedHello = (Hello) Proxy.newProxyInstance(
                getClass().getClassLoader(),                // 동적으로 생성되는 다이나믹 프록시 클래스의 로딩에 사용할 클래스 로더
                new Class[]{Hello.class},                 // 구현할 인터페이스
                new UppercaseHandler(new HelloTarget()));   // 부가기능과 위임 코드를 담은 invacationHandler

        assertThat(proxiedHello.sayHello("Toby"), is("HELLO TOBY"));
        assertThat(proxiedHello.sayHi("Toby"), is("HI TOBY"));
        assertThat(proxiedHello.sayThankYou("Toby"), is("THANK YOU TOBY"));
    }

    static class UppercaseHandler implements InvocationHandler {
        Object target;      // object for injection

        private UppercaseHandler(Object target) {
            this.target = target;
        }

        public Object invoke(Object proxy, Method method, Object[] args)
                throws Throwable {
            Object ret = method.invoke(target, args);       // 타겟으로 위임. 인터페이스의 메소드 호출에 모두 적용됨.
            if (ret instanceof String && method.getName().startsWith("say")) {
                return ((String) ret).toUpperCase();         // 호출한 메소드의 리턴 타입이 String인 경우에 한해서 대문자로 변경 하는 부가기능을 적용도록 수정.
            } else {
                return ret;
            }
        }
    }



    @Test
    // client using dynamic proxy with proxyFactoryBean
    public void proxyFactoryBean() {
        ProxyFactoryBean pfBean = new ProxyFactoryBean();
        pfBean.setTarget(new HelloTarget());        // 타겟 지정
        pfBean.addAdvice(new UppercaseAdvice());    // 부가기능을 담은 어드바이스 추가 (여러개 추가도 가능)

        Hello proxiedHello = (Hello) pfBean.getObject();    // FactoryBean이므로 getObject()로 생성된 프록시를 얻어온다.

        assertThat(proxiedHello.sayHello("Toby"), is("HELLO TOBY"));
        assertThat(proxiedHello.sayHi("Toby"), is("HI TOBY"));
        assertThat(proxiedHello.sayThankYou("Toby"), is("THANK YOU TOBY"));
    }

    static class UppercaseAdvice implements MethodInterceptor {
        public Object invoke(MethodInvocation invocation) throws Throwable {
            String ret = (String) invocation.proceed();     // reflection의 method와 달리 메소드 실행시 target object를 전달할 필요가 없다. (method invacation 은 method 정보와 함께 target object를 이미 알고 있음.)
            return ret.toUpperCase();                       // 부가기능 적용.
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
