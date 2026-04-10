package com.reviewy.ui;

import com.reviewy.model.Question;
import com.reviewy.service.QuizSession;
import com.reviewy.util.MarkdownParser;
import com.reviewy.util.MathRenderer;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class QuizView extends JPanel {
    private final QuizSession session;
    private final JTextPane statementPane;
    private final JPanel contentPanel;
    private final JPanel optionsPanel;
    private final JLabel progressLabel;
    private final JLabel scoreLabel;
    private final JButton nextBtn;
    private final JButton prevBtn;
    private final JButton restartBtn;
    private final ButtonGroup buttonGroup;

    public QuizView(QuizSession session, Runnable onRestart) {
        this.session = session;
        this.setLayout(new BorderLayout(10, 10));
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Progress and Score Header
        JPanel header = new JPanel(new BorderLayout());
        progressLabel = new JLabel();
        scoreLabel = new JLabel();
        header.add(progressLabel, BorderLayout.WEST);
        header.add(scoreLabel, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Content Area (Statement + Options) in a ScrollPane
        contentPanel = getJPanel();

        JPanel statementWrapper = new JPanel(new BorderLayout());
        statementWrapper.setOpaque(false);
        statementWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        statementPane = new JTextPane();
        statementPane.setContentType("text/html");
        statementPane.setEditable(false);
        statementPane.setOpaque(false);
        // Minimum size allows the layout manager to shrink the component during wrapping
        statementPane.setMinimumSize(new Dimension(0, 0));
        
        statementWrapper.add(statementPane, BorderLayout.CENTER);
        contentPanel.add(statementWrapper);

        contentPanel.add(Box.createVerticalStrut(20)); // Gap

        optionsPanel = new JPanel();
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
        optionsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        optionsPanel.setOpaque(false);
        buttonGroup = new ButtonGroup();
        contentPanel.add(optionsPanel);

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);

        // Footer Navigation
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        prevBtn = new JButton("Anterior");
        nextBtn = new JButton("Próxima");
        restartBtn = new JButton("Reiniciar");
        restartBtn.setVisible(false);

        prevBtn.addActionListener(e -> {
            session.previous();
            updateView();
        });

        nextBtn.addActionListener(e -> {
            session.next();
            updateView();
        });

        restartBtn.addActionListener(e -> {
            session.reset();
            restartBtn.setVisible(false);
            nextBtn.setVisible(true);
            prevBtn.setVisible(true);
            updateView();
            if (onRestart != null) onRestart.run();
        });

        footer.add(prevBtn);
        footer.add(nextBtn);
        footer.add(restartBtn);
        add(footer, BorderLayout.SOUTH);

        updateView();
    }

    private static class ScrollablePanel extends JPanel implements Scrollable {
        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }
        @Override
        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 16;
        }
        @Override
        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
            return visibleRect.height;
        }
        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }
        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }

    private JPanel getJPanel() {
        ScrollablePanel contentPanel = new ScrollablePanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        return contentPanel;
    }

    private void updateView() {
        MathRenderer.clearCache();
        
        Question current = session.getCurrentQuestion();
        String statementHtml = MarkdownParser.toHtml(current.getStatement());
        
        statementPane.setText(statementHtml);

        optionsPanel.removeAll();
        buttonGroup.clearSelection();

        boolean answered = session.isAnswered(current.getId());
        String userAnswer = session.getUserAnswerFor(current.getId());

        for (Map.Entry<String, String> entry : current.getOptions().entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            
            // Render basic HTML for the option content (No color logic here)
            String optionHtml = MarkdownParser.toHtml(value);
            
            // Container for Option + Feedback
            JPanel optionContainer = new JPanel();
            optionContainer.setLayout(new BoxLayout(optionContainer, BoxLayout.Y_AXIS));
            optionContainer.setAlignmentX(Component.LEFT_ALIGNMENT);
            optionContainer.setOpaque(false);

            JRadioButton rb = new JRadioButton(optionHtml);
            rb.setName(key);
            rb.setEnabled(true); 
            rb.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            if (answered && key.equalsIgnoreCase(userAnswer)) {
                rb.setSelected(true);
            }

            rb.addActionListener(e -> {
                if (session.isAnswered(current.getId())) {
                    updateView();
                    return;
                }
                session.validateAnswer(current.getId(), key);
                updateView();
            });

            buttonGroup.add(rb);
            optionContainer.add(rb);

            // Add feedback label if answered
            if (answered) {
                if (key.equalsIgnoreCase(current.getCorrect())) {
                    boolean isUserCorrect = userAnswer.equalsIgnoreCase(current.getCorrect());
                    String msg = isUserCorrect ? "✓ Acertou!" : "✓ Essa é a resposta correta";
                    JLabel feedback = new JLabel(msg);
                    feedback.setForeground(new Color(0, 136, 0)); // Green
                    feedback.setFont(feedback.getFont().deriveFont(Font.BOLD, 12f));
                    feedback.setBorder(BorderFactory.createEmptyBorder(0, 25, 5, 0));
                    feedback.setAlignmentX(Component.LEFT_ALIGNMENT);
                    optionContainer.add(feedback);
                } else if (key.equalsIgnoreCase(userAnswer)) {
                    JLabel feedback = new JLabel("✗ Você errou...");
                    feedback.setForeground(new Color(204, 0, 0)); // Red
                    feedback.setFont(feedback.getFont().deriveFont(Font.BOLD, 12f));
                    feedback.setBorder(BorderFactory.createEmptyBorder(0, 25, 5, 0));
                    feedback.setAlignmentX(Component.LEFT_ALIGNMENT);
                    optionContainer.add(feedback);
                }
            }

            optionsPanel.add(optionContainer);
            optionsPanel.add(Box.createVerticalStrut(10)); // Gap between containers
        }

        progressLabel.setText(String.format("Questão %d de %d (Restam: %d)", 
                session.getCurrentIndex() + 1, 
                session.getQuiz().getQuestions().size(), 
                session.getRemaining()));
        
        scoreLabel.setText(String.format("Acertos: %d | Erros: %d", 
                session.getHits(), 
                session.getMisses()));

        prevBtn.setEnabled(session.getCurrentIndex() > 0);
        nextBtn.setEnabled(session.hasMoreQuestions() && session.isAnswered(current.getId()));

        if (session.isLastQuestion() && session.isAnswered(current.getId())) {
            nextBtn.setVisible(false);
            prevBtn.setVisible(false);
            restartBtn.setVisible(true);
        }

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private String extractBody(String html) {
        if (html == null) return "";
        int start = html.indexOf("<body style='font-family: sans-serif; font-size: 14px;'>");
        if (start == -1) start = html.indexOf("<body>");
        if (start != -1) {
            start = html.indexOf(">", start) + 1;
            int end = html.lastIndexOf("</body>");
            if (end != -1) {
                return html.substring(start, end).trim();
            }
        }
        return html;
    }
}
