package easv.easv_tickets_bar.repo;

import easv.easv_tickets_bar.CustomExceptions.DataBaseConnectionException;
import easv.easv_tickets_bar.CustomExceptions.DuplicateException;
import easv.easv_tickets_bar.be.Ticket;
import easv.easv_tickets_bar.dal.TicketAccessObject;

import java.util.List;

public class TicketRepository {

    private TicketAccessObject tao;
    public TicketRepository() {
        this.tao = new TicketAccessObject();
    }

    public void createTicket(int id, String name, double priceDouble, String description) throws DataBaseConnectionException, DuplicateException {
        tao.createTicket(id, name, priceDouble, description);
    }

//    public List<Ticket> getTicketsByCoordinator(int id) throws DataBaseConnectionException {
//        return ticketDAO.getTicketsByCoordinator(id);
//    }

    public List<Ticket> getTicketsOfEvent(int id) throws DataBaseConnectionException {
        return tao.getTicketsOfEvent(id);
    }

    public boolean sellTicket(int id, int ticketTypeId, String name, String secondName, String email, List<String> ticketIds) throws Exception {
        return tao.sellTicket(id, ticketTypeId, name, secondName, email, ticketIds);
    }


    public void markQrCodeGenerated(String ticketId) throws DataBaseConnectionException {
        tao.markQrCodeGenerated(ticketId);
    }
}
