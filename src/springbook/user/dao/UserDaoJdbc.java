package springbook.user.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import springbook.user.domain.Level;
import springbook.user.domain.User;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;


public class UserDaoJdbc implements UserDao {

    private JdbcTemplate jdbcTemplate;

    private RowMapper<User> userMapper =
            new RowMapper<User>() {
                public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                    User user = new User();
                    user.setId(rs.getString("id"));
                    user.setName(rs.getString("name"));
                    user.setPassword(rs.getString("password"));
                    user.setEmail(rs.getString("email"));
                    user.setLevel(Level.valueOf(rs.getInt("level")));
                    user.setLogin(rs.getInt("login"));
                    user.setRecommend(rs.getInt("recommend"));
                    return user;
                }
            };
    private Map<String, String> sqlMap;

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void setSqlMap(Map<String, String> sqlMap) {
        this.sqlMap = sqlMap;
    }

    public void add(User user) {
        this.jdbcTemplate.update(
                this.sqlMap.get("add"),
                user.getId(), user.getName(), user.getPassword(), user.getEmail(),
                user.getLevel().intValue(), user.getLogin(), user.getRecommend()
        );
    }

    public User get(String id) {
        return this.jdbcTemplate.queryForObject(
                this.sqlMap.get("get"),
                new Object[]{id},
                this.userMapper
        );
    }

    public void deleteAll() {
        this.jdbcTemplate.update(this.sqlMap.get("deleteAll"));
    }

    public int getCount() {
        return this.jdbcTemplate.queryForInt(this.sqlMap.get("getCount"));
    }

    public List<User> getAll() {
        return this.jdbcTemplate.query(
                this.sqlMap.get("getAll"),
                this.userMapper
        );
    }

    public void update(User user) {
        this.jdbcTemplate.update(
                sqlMap.get("update"),
                user.getName(), user.getPassword(), user.getEmail(),
                user.getLevel().intValue(), user.getLogin(), user.getRecommend(),
                user.getId());
    }
}
