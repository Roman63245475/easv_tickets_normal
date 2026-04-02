package easv.easv_tickets_bar.gui;

import easv.easv_tickets_bar.CustomExceptions.DataBaseConnectionException;

public interface IRefreshable {
    void refreshTable();
    void restoreTimeLine();
}
