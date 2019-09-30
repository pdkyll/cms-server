package nl.menninga.menno.cms.service;

import java.util.Objects;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import nl.menninga.menno.cms.entity.Order;
import nl.menninga.menno.cms.repository.OrderRepository;

@Service
public class OrderService {

	@Autowired
	private OrderRepository orderRepository;
	
	public Page<Order> getOrders(Integer page){
		return orderRepository.findAll(PageRequest.of((page != null ? page : 0), 20, Sort.by("creationTime").descending()));
	}
	
	public Order getOrderById(Long id) throws NotFoundException {
		Order order = orderRepository.getOne(id);
		if(Objects.isNull(order)) {
			throw new NotFoundException();
		}
		return order;
	}
	
	@Transactional
	public void deleteOrder(Long id) throws NotFoundException {
		Order order = orderRepository.getOne(id);
		if(Objects.isNull(order)) {
			throw new NotFoundException();
		}
		orderRepository.delete(order);
	}
	
	public Order createOrder(Order order) throws IllegalArgumentException {
		return orderRepository.saveAndFlush(order);
	}

	public Order updateOrder(Order order) throws NotFoundException {
		Order origOrder = orderRepository.getOne(order.getId());
		if(Objects.isNull(origOrder)) {
			throw new NotFoundException();
		}
		return orderRepository.saveAndFlush(order);
	}
}
