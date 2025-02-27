package view;

import javax.swing.*;
import model.RankingEntry;
import java.util.List;

public class RankingView extends JFrame {
    public RankingView(List<RankingEntry> ranking) {
        super("High Scores");
        setSize(400,400);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (RankingEntry entry : ranking) {
            listModel.addElement(entry.toString());
        }

        JList<String> list = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(list);

        add(scrollPane);
    }
}
