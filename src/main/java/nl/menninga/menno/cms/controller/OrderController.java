package nl.menninga.menno.cms.controller;

import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import nl.menninga.menno.cms.entity.Order;
import nl.menninga.menno.cms.service.EmailService;
import nl.menninga.menno.cms.service.OrderService;

@RestController
@CrossOrigin
public class OrderController {

	private static final Logger LOGGER = LoggerFactory.getLogger(OrderController.class);
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private EmailService emailService;
	
	@Value("${spring.mail.username}")
	private String ownMail;
	
	@GetMapping("api/orders/{page}")
	@PreAuthorize("hasRole('CMS_ADMIN')")
	public ResponseEntity<?> orderGet(@PathVariable(required = false) Integer page) {
		Page<Order> orders = orderService.getOrders(page);
		return ResponseEntity.ok(orders);
    }
	
	@GetMapping("api/order/{id}")
	@PreAuthorize("hasRole('CMS_ADMIN')")
	public ResponseEntity<?> orderGet(@PathVariable Long id) {
		Order order = null;		
		try {
			order = orderService.getOrderById(id);
		} catch (NotFoundException e) {
			LOGGER.info("Entity could not be found!", e);
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(order);
    }

	@PostMapping("api/order")
    public ResponseEntity<?> orderPost(@RequestBody Order order) {
		ObjectMapper jsonObjectMapper = new ObjectMapper();
		try {
			order = orderService.createOrder(order);
			if(ownMail != null && !ownMail.isEmpty() && ownMail.contains("@")) {
				emailService.sendOrderMessage(ownMail, order);
			}
			emailService.sendOrderMessage(order.getEmail(), order);
			order.setEmailSent(true);
			order = orderService.updateOrder(order);
		} catch (IllegalArgumentException e) {
			LOGGER.info("Entity could not be created!", e);
			return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(e);
		}catch(MailSendException e) {
			LOGGER.info("Email could not be sent!", e);
			return ResponseEntity.ok("{\"order\": " + jsonObjectMapper.valueToTree(order) + ", \"error\": " + true + "}");
		}catch (MailAuthenticationException e) {
			LOGGER.info("Email could not be sent!", e);
			return ResponseEntity.ok("{\"order\": " + jsonObjectMapper.valueToTree(order) + ", \"error\": " + true + "}");
		}catch (MailException e) {
			LOGGER.info("Email could not be sent!", e);
			return ResponseEntity.ok("{\"order\": " + jsonObjectMapper.valueToTree(order) + ", \"error\": " + true + "}");
		} catch (JsonProcessingException e) {
			LOGGER.info("Email could not be created!", e);
			return ResponseEntity.ok("{\"order\": " + jsonObjectMapper.valueToTree(order) + ", \"error\": " + true + "}");
		} catch (MessagingException e) {
			LOGGER.info("Email could not be created!", e);
			return ResponseEntity.ok("{\"order\": " + jsonObjectMapper.valueToTree(order) + ", \"error\": " + true + "}");
		} catch (NotFoundException e) {
			LOGGER.info("Entity could not be updated!", e);
			return ResponseEntity.ok("{\"order\": " + jsonObjectMapper.valueToTree(order) + ", \"error\": " + true + "}");
		}
		return ResponseEntity.ok(order);
    }

	@PutMapping("api/order")
    @PreAuthorize("hasRole('CMS_ADMIN')")
    public ResponseEntity<?> orderPut(@RequestBody Order order) {
		try {
			order = orderService.updateOrder(order);
		} catch (NotFoundException e) {
			LOGGER.info("Entity to update could not be found!", e);
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(order);
    }
	
	@DeleteMapping("api/order/{id}")
	@PreAuthorize("hasRole('CMS_ADMIN')")
    public ResponseEntity<?> orderDelete(@PathVariable Long id) {
		try {
	        orderService.deleteOrder(id);
		} catch (IllegalArgumentException e) {
			LOGGER.info("Entity could not be deleted!", e);
			return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(e);
		} catch (NotFoundException e) {
			LOGGER.info("Entity to delete could not be found!", e);
			return ResponseEntity.notFound().build();
		}
        return ResponseEntity.ok("Order deleted.");
    }
}