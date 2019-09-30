package nl.menninga.menno.cms.util;

import org.springframework.stereotype.Component;

import nl.menninga.menno.cms.model.OrderItem;

@Component("emailTemplateUtil")
public class EmailTemplateUtil {

	public Double calculateTotalPriceItem(OrderItem orderItem) {
		Double price = (orderItem.getQuantity() * Double.parseDouble(orderItem.getPrice().replaceAll(",", ".")));
		return price;
	}
	
	public Double calculateTotalPriceItems(OrderItem[] orderItems) {
		Double price = 0d;
		for(OrderItem orderItem : orderItems) {
			price = (price + calculateTotalPriceItem(orderItem));
		}
		return price;
	}
}
