package service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import dao.DeliveryDao;
import dao.OrderDao;
import dao.UserDao;
import dto.OrderDto;
import entity.Delivery;
import entity.Order;
import entity.OrderStatus;
import exception.ForbiddenException;
import exception.NotFoundException;
import exception.UnauthorizedUserException;
import util.SearchUtil;

public class DeliveryService {

    private final OrderDao orderDao;
    private final UserDao userDao;
    private final DeliveryDao deliveryDao;

    public DeliveryService(){
        orderDao = new OrderDao();
        userDao = new UserDao();
        deliveryDao = new DeliveryDao();
    }

    public List<OrderDto> getAvailableOrders() {
        return orderDao.findAll().stream().filter(
                o ->(
                        o.getStatus().equals(OrderStatus.finding_courier)
                ) && o.getDelivery() == null
        ).map(OrderDto::new).toList();
    }

    public OrderDto changeOrderStatus(Delivery delivery, Long order_id, String newStaus) {

        Order order = orderDao.findById(order_id);
        if(order == null) throw new NotFoundException("This order doesn't exist.");


        if(order.getDelivery() != null && (order.getDelivery().getId() != delivery.getId()))
            throw new ForbiddenException("This Order has already accepted.");

        OrderStatus status = OrderStatus.strToStatus(newStaus);
        if(!status.equals(OrderStatus.completed) && !status.equals(OrderStatus.on_the_way)
                && !status.equals(OrderStatus.cancelled) && !status.equals(OrderStatus.accepted)
                && !status.equals(OrderStatus.finding_courier)
        )
            throw new UnauthorizedUserException("You are not authorized for this action");
        
        order.setDelivery(delivery);
        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());
        delivery.addOrder(order);

        orderDao.update(order);
        userDao.update(delivery);
        deliveryDao.update(delivery);

        return new OrderDto(order);
    }

    public List<OrderDto> searchDeliveryHistory(String search, String vendor, String user, Delivery delivery) {

        String searchFilter = (search == null || search.isBlank()) ? "" : search.toLowerCase();
        String vendorFilter = (vendor == null || vendor.isBlank()) ? "" : vendor.toLowerCase();
        String userFilter = (user == null || user.isBlank()) ? "" : user.toLowerCase();

        List<Order> allOrders = orderDao.findAll().stream()
                .filter(o -> o.getDelivery() != null && o.getDelivery().getId().equals(delivery.getId())).toList();

        List<String> searchFields = List.of("deliveryAddress", "coupon.code", "status",
                "restaurant.name", "customer.fullName", "createdAt", "updatedAt");

        Map<String, String> filters = new HashMap<>();
        if (!vendorFilter.isBlank()) filters.put("restaurant.name", vendorFilter);
        if (!userFilter.isBlank()) filters.put("customer.fullName", userFilter);


        List<Order> result =
                SearchUtil.search(allOrders, Order.class, searchFilter, searchFields, filters);

        return result.stream().map(OrderDto::new).toList();
    }

}
