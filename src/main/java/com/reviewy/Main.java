package com.reviewy;

import com.formdev.flatlaf.FlatLightLaf;
import com.reviewy.model.Quiz;
import com.reviewy.service.QuizService;
import com.reviewy.service.QuizSession;
import com.reviewy.ui.QuizView;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

public class Main {
    public static void main(String[] args) {
        // Register custom protocol for memory-based images (LaTeX)
        com.reviewy.util.MemoryURLStreamHandler.register();

        // Set Look and Feel
        FlatLightLaf.setup();

        SwingUtilities.invokeLater(() -> {
            try {
                // File selection dialog
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Selecione o arquivo da Prova (YAML)");
                fileChooser.setFileFilter(new FileNameExtensionFilter("Arquivos YAML (*.yaml, *.yml)", "yaml", "yml"));
                
                // Show in the current directory as start
                fileChooser.setCurrentDirectory(new File("."));

                int result = fileChooser.showOpenDialog(null);
                
                if (result != JFileChooser.APPROVE_OPTION) {
                    System.out.println("Nenhum arquivo selecionado. Saindo...");
                    System.exit(0);
                }

                File selectedFile = fileChooser.getSelectedFile();
                String yamlPath = selectedFile.getAbsolutePath();

                QuizService service = new QuizService();
                Quiz quiz = service.loadQuizFromYaml(yamlPath);
                
                if (quiz == null || quiz.getQuestions() == null || quiz.getQuestions().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "O arquivo selecionado não contém questões válidas.");
                    System.exit(0);
                }

                QuizSession session = new QuizSession(quiz);

                JFrame frame = new JFrame(quiz.getTitle() != null ? quiz.getTitle() : "Reviewy Quiz");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(800, 700);
                frame.setLocationRelativeTo(null);

                QuizView quizView = new QuizView(session, () -> {
                    // Logic on restart if needed
                });
                frame.add(quizView);

                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Erro ao carregar a prova: " + e.getMessage());
                System.exit(1);
            }
        });
    }
}
