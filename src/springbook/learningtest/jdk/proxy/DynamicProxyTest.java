package springbook.learningtest.jdk.proxy;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.Test;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;

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

    @Test
    // client using dynamic proxy with proxyFactoryBean 2 (pointcut applied)
    public void pointcutAdvisor() {
        ProxyFactoryBean pfBean = new ProxyFactoryBean();
        pfBean.setTarget(new HelloTarget());

        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();           // method 이름을 비교해서 대상을 선정하는 알고리즘을 제공하는 Pointcut 생성
        pointcut.setMappedName("sayH*");                                            // 이름 비교조건 설정. sayH로 시작하는 모든 메소드를 선택하도록 함

        pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvice())); // pointcut 과 advise를 묶어서 하나의 advisor로 추가

        Hello proxiedHello = (Hello) pfBean.getObject();

        assertThat(proxiedHello.sayHello("Toby"), is("HELLO TOBY"));
        assertThat(proxiedHello.sayHi("Toby"), is("HI TOBY"));
        assertThat(proxiedHello.sayThankYou("Toby"), is("Thank You Toby"));     // method 이름이 pointcut 선정 조건에 맞지않아 부가기능이 적용되지 않는다.
    }

    static class UppercaseAdvice implements MethodInterceptor {
        public Object invoke(MethodInvocation invocation) throws Throwable {
            String ret = (String) invocation.proceed();     // reflection의 method와 달리 메소드 실행시 target object를 전달할 필요가 없다. (method invacation 은 method 정보와 함께 target object를 이미 알고 있음.)
            return ret.toUpperCase();                       // 부가기능 적용.
        }
    }

    @Test
    public void classNamePointcutAdvisor() {

        // 포인트컷 준비
        NameMatchMethodPointcut classMethodPointcut = new NameMatchMethodPointcut() {
            public ClassFilter getClassFilter() {   // 익명 내부 클래스 방식으로 클래스를 정의
                return new ClassFilter() {
                    public boolean matches(Class<?> clazz) {
                        return clazz.getSimpleName().startsWith("HelloT");  // class 이름이 HelloT 로 시작하는 것만 선정하도록 설정.
                    }
                };
            }
        };
        classMethodPointcut.setMappedName("sayH*"); // method 이름이 sayH로 시작하는 것만 선정하도록 설정.

        // 테스트
        checkAdviced(new HelloTarget(), classMethodPointcut, true);     // 적용 클래스

        class HelloWorld extends HelloTarget {};
        checkAdviced(new HelloWorld(), classMethodPointcut, false);     // 비적용 클래스

        class HelloToby extends HelloTarget {};
        checkAdviced(new HelloToby(), classMethodPointcut, true);       // 적용 클래스
    }


    private void checkAdviced(Object target, Pointcut pointcut, boolean adviced) {
        ProxyFactoryBean pfBean = new ProxyFactoryBean();
        pfBean.setTarget(target);
        pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvice()));
        Hello proxiedHello = (Hello) pfBean.getObject();

        if (adviced) {
            assertThat(proxiedHello.sayHello("Toby"), is("HELLO TOBY"));    // method 선정방식을 통한 어드바이스 적용
            assertThat(proxiedHello.sayHi("Toby"), is("HI TOBY"));          // method 선정방식을 통한 어드바이스 적용
            assertThat(proxiedHello.sayThankYou("Toby"), is("Thank You Toby"));
        }
        else {
            // advice 적용 대상 아님
            assertThat(proxiedHello.sayHello("Toby"), is("Hello Toby"));
            assertThat(proxiedHello.sayHi("Toby"), is("Hi Toby"));
            assertThat(proxiedHello.sayThankYou("Toby"), is("Thank You Toby"));
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
