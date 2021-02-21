package springbook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.MailSender;
import springbook.user.dao.UserDao;
import springbook.user.service.DummyMailSender;
import springbook.user.service.UserService;
import springbook.user.service.UserServiceTest.TestUserService;

@Configuration
public class TestAppContext {


    @Bean
    public UserService testUserService() {
        TestUserService testService = new TestUserService();
        return testService;
    }

    @Bean
    public MailSender mainSender() {
        return new DummyMailSender();
    }
}
