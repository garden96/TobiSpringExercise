package springbook.user.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Properties;


public class UserService {
	public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
	public static final int MIN_RECCOMEND_FOR_GOLD = 30;

	private UserDao userDao;

	private PlatformTransactionManager transactionManager;

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	public void upgradeLevels() {
		TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition());

		try {									   
            List<User> users = userDao.getAll();

            for(User user : users) {
                if (canUpgradeLevel(user)) {
                    upgradeLevel(user);
                }
            }
			this.transactionManager.commit(status);
		} catch (RuntimeException e) {
			this.transactionManager.rollback(status);
            throw e;
        }
    }

	private boolean canUpgradeLevel(User user) {
	    Level currentLevel = user.getLevel();

	    switch (currentLevel) {
		case BASIC: return (user.getLogin() >= MIN_LOGCOUNT_FOR_SILVER); 
		case SILVER: return (user.getRecommend() >= MIN_RECCOMEND_FOR_GOLD);
		case GOLD: return false;
		default: throw new IllegalArgumentException("Unknown Level: " + currentLevel); 
        }
	}

	protected void upgradeLevel(User user) {
        user.upgradeLevel();
        userDao.update(user);
		sendUpgradeEMail(user);
    }

	private void sendUpgradeEMail(User user) {
        Properties props = new Properties();
        props.put("mail.smtp.host", "mail.sunnygarden.net");
        Session s = Session.getInstance(props, null);

        MimeMessage message = new MimeMessage(s);
        try{
            message.setFrom( new InternetAddress("admin@sunnygarden.net"));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(user.getEmail()));
            message.setSubject("Upgrade 안내");
            message.setText("사용자님의 등급이 " + user.getLevel().name());

            Transport.send(message);
        } catch (AddressException e) {
            throw new RuntimeException();
        } catch (MessagingException e) {
            throw new RuntimeException();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException();
        }
	}

	public void add(User user) {
		if (user.getLevel() == null) user.setLevel(Level.BASIC);
		userDao.add(user);
	}

}

