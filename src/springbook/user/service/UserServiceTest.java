package springbook.user.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="/test-applicationContext.xml")
public class UserServiceTest {

	@Autowired 	UserService userService;
	@Autowired  UserDao userDao;

	List<User> users;	// test fixture

	@Before
	public void setUp() {
		users = Arrays.asList(
                new User("nathan", "안정원", "p1", Level.BASIC, 49, 0),
                new User("sunny", "현선", "p2", Level.BASIC, 50, 0),
                new User("jane", "안재인", "p3", Level.SILVER, 60, 29),
                new User("junu", "안준우", "p4", Level.SILVER, 60, 30),
                new User("nayoon", "현나윤", "p5", Level.GOLD, 100, 100)
		);
	}

    @Test
    /**
     * 빈등록 설정이 잘 되어 빈이 생성되는지 확인
     */
    public void bean() {
        assertThat(this.userService, is(notNullValue()));
    }

    @Test
	public void upgradeLevels() {
		userDao.deleteAll();
		for(User user : users) userDao.add(user);

		userService.upgradeLevels();

		checkLevel(users.get(0), Level.BASIC);
		checkLevel(users.get(1), Level.SILVER);
		checkLevel(users.get(2), Level.SILVER);
		checkLevel(users.get(3), Level.GOLD);
		checkLevel(users.get(4), Level.GOLD);
	}

	private void checkLevel(User user, Level expectedLevel) {
	    User userUpdate = userDao.get(user.getId());
	    assertThat(userUpdate.getLevel(), is(expectedLevel));
    }

	@Test 
	public void add() {
		userDao.deleteAll();

		User userWithLevel = users.get(4);	  // GOLD 레벨
		User userWithoutLevel = users.get(0);  
		userWithoutLevel.setLevel(null);

		userService.add(userWithLevel);	  
		userService.add(userWithoutLevel);

		User userWithLevelRead = userDao.get(userWithLevel.getId());
		User userWithoutLevelRead = userDao.get(userWithoutLevel.getId());

		assertThat(userWithLevelRead.getLevel(), is(userWithLevel.getLevel())); 
		assertThat(userWithoutLevelRead.getLevel(), is(Level.BASIC));
	}
}

