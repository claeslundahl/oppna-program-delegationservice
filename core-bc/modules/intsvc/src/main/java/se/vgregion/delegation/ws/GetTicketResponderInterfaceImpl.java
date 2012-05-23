/**
 * 
 */
package se.vgregion.delegation.ws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.riv.authorization.delegation.getticket.v1.rivtabp21.GetTicketResponderInterface;
import se.riv.authorization.delegation.getticketresponder.v1.GetTicketResponseType;
import se.riv.authorization.delegation.getticketresponder.v1.GetTicketType;
import se.vgregion.ticket.Ticket;
import se.vgregion.ticket.TicketException;
import se.vgregion.ticket.TicketManager;
import se.vgregion.web.dto.TicketDto;

/**
 * @author Simon Göransson - simon.goransson@monator.com - vgrid: simgo3
 * 
 */
public class GetTicketResponderInterfaceImpl implements GetTicketResponderInterface {
    static private final Logger logger = LoggerFactory.getLogger(GetTicketResponderInterfaceImpl.class);

    TicketManager ticketManager;

    public GetTicketResponderInterfaceImpl(TicketManager ticketManager) {
        super();
        this.ticketManager = ticketManager;
    }

    @Override
    public GetTicketResponseType getTicket(String logicalAddress, GetTicketType parameters) {

        GetTicketResponseType getTicketResponseType = new GetTicketResponseType();
        try {

            System.out.println("My logg - ");
            System.out.println("My logg - parameters.getServiceId() = " + parameters.getServiceId());

            Ticket ticket = ticketManager.solveTicket(parameters.getServiceId());
            TicketDto ticketDto = new TicketDto(ticket);
            getTicketResponseType.setTicket(ticketDto.toString());
        } catch (TicketException e) {
            logger.error("Faile to crate ticket : " + e.getMessage());
        }

        return getTicketResponseType;

    }
}
