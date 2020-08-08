package springbook.user.dao;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import springbook.user.domain.User;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
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


	public User get(String id) throws SQLException {

		Connection c = this.dataSource.getConnection();
		PreparedStatement ps = c.prepareStatement("select * from users where id = ?");
            ps.setString(1, id);
		ResultSet rs = ps.executeQuery();

		User user = null;
        if (rs.next()) {
            user = new User();
            user.setId(rs.getString("id"));
            user.setName(rs.getString("name"));
            user.setPassword(rs.getString("password"));
        }

        rs.close();
        ps.close();
        c.close();

        if (user == null) throw new EmptyResultDataAccessException(1);

        return user;
    }

	public void deleteAll() {
        this.jdbcTemplate.update("delete from users");
	}

	public int getCount() throws SQLException  {

		Connection c = dataSource.getConnection();
		PreparedStatement ps = c.prepareStatement("select count(*) from users");

		ResultSet rs = ps.executeQuery();
        rs.next();

		int count = rs.getInt(1);

        rs.close();
        ps.close();
        c.close();
	
        return count;
    }
}
