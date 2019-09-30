package nl.menninga.menno.cms.service;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.expression.ThymeleafEvaluationContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import nl.menninga.menno.cms.entity.Order;
import nl.menninga.menno.cms.model.OrderItem;

@Service
public class MailContentBuilder implements ApplicationContextAware{

	private TemplateEngine templateEngine;
	private ApplicationContext applicationContext;

	@Autowired
	public MailContentBuilder(TemplateEngine templateEngine) {
		this.templateEngine = templateEngine;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public String build(Order order) throws JsonProcessingException {
		Context context = new Context();
		context.setVariable(ThymeleafEvaluationContext.THYMELEAF_EVALUATION_CONTEXT_CONTEXT_VARIABLE_NAME,
                new ThymeleafEvaluationContext(applicationContext, null));
		OrderItem[] orderItems = convertJsonNodeToObject(order.getOrderItems(), OrderItem[].class);
		context.setVariable("order", order);
		context.setVariable("orderItems", orderItems);
		return templateEngine.process("orderMailTemplate", context);
	}
	
	private <T> T convertJsonNodeToObject(JsonNode jsonNode, Class<T> toClass) throws JsonProcessingException {
		ObjectMapper jsonObjectMapper = new ObjectMapper();
		T convertedClass = jsonObjectMapper.treeToValue(jsonNode, toClass);
		return convertedClass;
	}
}
