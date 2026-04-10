# Reviewy Quiz System

Sistema de desktop desenvolvido em Java 21 para leitura de arquivos YAML e aplicação de provas técnicas com interface gráfica.

## Funcionalidades

- Carregamento Dinâmico: Permite ao usuário selecionar qualquer arquivo YAML local para iniciar o quiz.
- Renderização de Conteúdo: Suporte a Markdown para enunciados e opções, incluindo blocos de código formatados.
- Suporte Matemático: Integração com JLaTeXMath para exibição de fórmulas complexas (inline e em bloco).
- Feedback Visual: Mensagens explícitas de acerto e erro exibidas imediatamente após a resposta.
- Controle de Sessão: Navegação entre questões (voltar/avançar), contador de progresso, placar de acertos/erros e opção de reiniciar ao final.
- Protocolo de Memória: Uso de protocolo customizado (mem:) para carregar imagens geradas em tempo de execução sem dependência de arquivos temporários.

## Requisitos

- Java JDK 21 ou superior.
- Apache Maven 3.8 ou superior.

## Como Executar

1. Compile o projeto utilizando o Maven:
   ```bash
   mvn clean compile
   ```

2. Inicie a aplicação:
   ```bash
   mvn exec:java -Dexec.mainClass="com.reviewy.Main"
   ```

3. Selecione o arquivo YAML desejado (exemplo: `yaml_example/prova_completa.yaml`) no diálogo que será aberto.

## Estrutura do Arquivo YAML

O sistema espera o seguinte formato para os arquivos de prova:

```yaml
title: "Título da Prova"
questions:
  - id: q1
    type: single_choice
    correct: a
    statement: |
      Texto do enunciado em Markdown.
      ```java
      // Exemplo de código
      ```
    options:
      a: "Texto da opção A"
      b: "Texto da opção B"
      c: "Texto da opção C"
      d: "Texto da opção D"
```

## Dependências Principais

- Jackson YAML: Processamento de arquivos de configuração.
- FlatLaf: Look and Feel moderno para a interface Swing.
- Commonmark: Conversão de Markdown para HTML.
- JLaTeXMath: Renderização de fórmulas LaTeX.
