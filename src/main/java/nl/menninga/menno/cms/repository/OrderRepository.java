package nl.menninga.menno.cms.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import nl.menninga.menno.cms.entity.Order;

@Repository
@Transactional
public interface OrderRepository extends JpaRepository<Order, Long> {
	
}
