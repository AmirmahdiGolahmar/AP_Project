package service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collector;

import dao.DeliveryDao;
import dao.OrderDao;
import dao.UserDao;
import dto.DeliverySearchRequestDto;
import dto.OrderDto;
import entity.Delivery;
import entity.Order;
import entity.OrderStatus;
import exception.ForbiddenException;
import exception.NotFoundException;
import exception.UnauthorizedUserException;

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
        return orderDao.findAll().stream().filter(o -> o.getStatus().equals(OrderStatus.submitted) &&
            o.getDelivery() == null).map(OrderDto::new).toList();
    }

    public OrderDto changeOrderStatus(String userId, Long order_id, String newStaus) {

        Order order = orderDao.findById(order_id);
        if(order == null) throw new NotFoundException("This order doesn't exis.");

        Delivery delivery = (Delivery) userDao.findById((long)Integer.parseInt(userId));

        if(order.getDelivery() != null && (order.getDelivery().getId() != (long)Integer.parseInt(userId)))
            throw new ForbiddenException("This Order has already accepted.");

        OrderStatus status = OrderStatus.strToStatus(newStaus);
        if(!status.equals(OrderStatus.completed) && !status.equals(OrderStatus.on_the_way))
            throw new UnauthorizedUserException("You are not authroized for this action");
        
        order.setDelivery(delivery);
        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());
        delivery.addOrder(order);

        orderDao.update(order);
        userDao.update(delivery);
        deliveryDao.update(delivery);

        return new OrderDto(order);
    }

    public List<OrderDto> deliveryHistory(DeliverySearchRequestDto request, String userId) {
        Delivery delivery = deliveryDao.findById((long)Integer.parseInt(userId));

        String userFilter = request.getUser() == null ? null : request.getUser().toLowerCase();
        String vendorFilter = request.getVendor() == null ? null : request.getVendor().toLowerCase();
        String searchFilter = request.getSearch() == null ? null : request.getSearch().toLowerCase();

        return delivery.getOrders().stream()
            .filter(o -> (userFilter == null || o.getCustomer().getFullName().toLowerCase().contains(userFilter)) &&
                        (vendorFilter == null || o.getRestaurant().getName().toLowerCase().contains(vendorFilter)) &&
                        (searchFilter == null || o.getStatus().toString().toLowerCase().contains(searchFilter) ||
                         o.getCustomer().getFullName().toLowerCase().contains(searchFilter) ||
                         o.getRestaurant().getName().toLowerCase().contains(searchFilter) ||
                         o.getDeliveryAddress().toLowerCase().contains(searchFilter)))
            .map(OrderDto::new).toList();
    }

}
