package easv.easv_tickets_bar.repo;

import easv.easv_tickets_bar.CustomExceptions.DataBaseConnectionException;
import easv.easv_tickets_bar.CustomExceptions.DuplicateException;
import easv.easv_tickets_bar.be.TicketEvent;
import easv.easv_tickets_bar.dal.TicketAccessObject;

import java.util.List;

public class TicketRepository {

    private TicketAccessObject ticketDAO;
    public TicketRepository() {
        this.ticketDAO = new TicketAccessObject();
    }

    public void createTicket(int id, String name, double priceDouble, String description) throws DataBaseConnectionException, DuplicateException {
        ticketDAO.createTicket(id, name, priceDouble, description);
    }

    public List<TicketEvent> getTicketsByCoordinator(int id) throws DataBaseConnectionException {
        return ticketDAO.getTicketsByCoordinator(id);
    }

    public List<TicketEvent> getTicketsOfEvent(int id) throws DataBaseConnectionException {
        return ticketDAO.getTicketsOfEvent(id);
    }

    public void sellTicket(int id, String name, String email, int quantity, int available) throws Exception {
        ticketDAO.sellTicket(id, name, email, quantity, available);
    }


}
