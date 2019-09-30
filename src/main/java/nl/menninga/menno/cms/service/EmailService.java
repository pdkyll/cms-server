package nl.menninga.menno.cms.service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;

import nl.menninga.menno.cms.entity.Order;

@Service
public class EmailService {

	@Autowired
	private JavaMailSender javaMailSender;
	
	@Autowired
	private MailContentBuilder mailContentBuidler;

	public void sendOrderMessage(String mailTo, Order order) throws JsonProcessingException, MailException, MessagingException, MailSendException, MailAuthenticationException {
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
		helper.setTo(mailTo);
		helper.setSubject("Bevestiging bestelling aanvraag onder referentie: " + order.getOrderReference());
		String text = mailContentBuidler.build(order);
		helper.setText(text, true);
		javaMailSender.send(mimeMessage);
	}
}