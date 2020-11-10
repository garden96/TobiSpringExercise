package springbook.user.service;

import org.springframework.transaction.annotation.Transactional;
import springbook.user.domain.User;

import java.util.List;

public interface UserService {
    void add(User user);
    void deleteAll();
    void update(User user);

    @Transactional(readOnly=true)
    User get(String id);

    @Transactional(readOnly=true)
    List<User> getAll();

    void upgradeLevels();
}
