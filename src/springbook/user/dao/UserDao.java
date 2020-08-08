package springbook.user.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import springbook.user.domain.User;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;


public class UserDao {

    private DataSource dataSource;
    private JdbcTemplate jdbcTemplate;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


	public void add(final User user) {
        this.jdbcTemplate.update(
                "insert into users(id, name, password) values(?,?,?)",
                user.getId(), user.getName(), user.getPassword()
        );
    }


	public User get(String id) {
		return this.jdbcTemplate.queryForObject(
		        "select * from users where id = ?",
                new Object[] {id},
                new RowMapper<User>() {
                    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                        User user = new User();
                        user.setId(rs.getString("id"));
                        user.setName(rs.getString("name"));
                        user.setPassword(rs.getString("password"));
                        return user;
                    }
		        }
		);
    }

	public void deleteAll() {
        this.jdbcTemplate.update("delete from users");
	}

	public int getCount() {
        return this.jdbcTemplate.queryForInt("select count(*) from users");
    }

}
