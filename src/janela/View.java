package janela;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;

/**
 *
 * @author Rodrigo
 */
public class View {

    private static final String LITERAL_OSSOS = "Ossos";
    private static final String LITERAL_MEDIASTINO = "Mediastino";
    private static final String LITERAL_PULMOES = "Pulmões";
    private static final String LITERAL_CUSTOMIZADO = "Customizado";

    private final Controller controle;

    private JFrame janela;
    private JMenuBar menu;
    private JPanel painelImagem;
    private JPanel painelCentral;
    private JPanel painelRodape;
    private JPanel painelPrincipal;

    private JFormattedTextField windowLevel;
    private JFormattedTextField windowWidth;
    private JComboBox templateWLWW;
    private JCheckBox segPulmaoEsq;
    private JCheckBox segPulmaoDir;
    private JLabel sliceCorrente;
    private JSlider slider;

    JLabel labelNomeArquivo;
    JLabel labelIndiceImagem;
    JLabel espessuraFatia;
    JLabel labelCoordenadas; 
    JLabel labelHU; 
    JLabel labelRGB; 
    JLabel tamPulmaoEsquerdo; 
    JLabel tamPulmaoDireito; 
    
    public View(Controller controle) {
        this.controle = controle;
    }

    public void exibe() {
        janela = new JFrame();
        janela.setTitle("Segmentação automática de órgãos em exames de TC");
        janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        janela.setBounds(1, 1, 774, 665);
        janela.setLocationRelativeTo(null);
        janela.setLayout(new BorderLayout());

        criaMenus();

        //painel da imagem, fica dentro do painel central
        painelImagem = new JPanel(new BorderLayout());
        //painel central, contem o painel com a imagem e com as informacoes
        painelCentral = new JPanel(new BorderLayout());
        painelCentral.add(painelImagem, BorderLayout.WEST);
        //painel do rodape, contem os campos do rodape da janela
        painelRodape = new JPanel();
        painelRodape.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        //painel principal, contem todos outros paineis (com excecao do painel do frame
        painelPrincipal = new JPanel(new BorderLayout());
        painelPrincipal.add(painelCentral, BorderLayout.CENTER);
        painelPrincipal.add(painelRodape, BorderLayout.SOUTH);
        //adiciona o painel principal no painel do frame
        janela.getContentPane().add(painelPrincipal, BorderLayout.CENTER);

        criaRodape();

        slider = new JSlider(JSlider.VERTICAL, 0, 10, 0);
        slider.setInverted(true);
        slider.setToolTipText("Seleciona a fatia do exame que será utilizada");
        slider.setMinorTickSpacing(1);
        slider.setPaintTicks(true);
        slider.addChangeListener((ChangeEvent e) -> {
            atualizaImagem();
        });
        janela.getContentPane().add(slider, BorderLayout.WEST);

        sliceCorrente = new JLabel();
        sliceCorrente.setPreferredSize(new Dimension(512, 512));
        sliceCorrente.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                atualizaCoordenadas(e.getX(), e.getY());
            }
        });
        painelImagem.add(sliceCorrente);

        criaInformacoes();

        janela.pack();
        janela.setVisible(true);
    }

    private void criaMenus() {
        menu = new JMenuBar();
        JMenu menuArquivo = new JMenu("Arquivo");
        menu.add(menuArquivo);
        JMenuItem submenuSelecionarDiretorio = new JMenuItem("Selecionar diretório");
        submenuSelecionarDiretorio.addActionListener((ActionEvent e) -> {
            controle.selecionarDirArq(Controller.DIRETORIO);
        });
        menuArquivo.add(submenuSelecionarDiretorio);
        JMenuItem submenuSelecionarArquivo = new JMenuItem("Selecionar arquivo");
        submenuSelecionarArquivo.addActionListener((ActionEvent e) -> {
            controle.selecionarDirArq(Controller.ARQUIVO);
        });
        menuArquivo.add(submenuSelecionarArquivo);

        JMenuItem submenuSalvarF = new JMenuItem("Salvar fatia");
        submenuSalvarF.addActionListener((ActionEvent e) -> {
            if (!controle.temExameCarregado()) {
                JOptionPane.showMessageDialog(null, "Não há nenhum exame carregado.");
                return;
            }

            controle.salvarFatia();
        });
        menuArquivo.add(submenuSalvarF);
        
        JMenuItem submenuSalvarE = new JMenuItem("Salvar exame");
        submenuSalvarE.addActionListener((ActionEvent e) -> {
            if (!controle.temExameCarregado()) {
                JOptionPane.showMessageDialog(null, "Não há nenhum exame carregado.");
                return;
            }

            controle.salvarExame();
        });
        menuArquivo.add(submenuSalvarE);
        
        
        JMenuItem submenuSair = new JMenuItem("Sair");
        submenuSair.addActionListener((ActionEvent e) -> {
            janela.dispose();
        });
        menuArquivo.add(submenuSair);
        JMenu menuSobre = new JMenu("Sobre");
        menu.add(menuSobre);
        JMenuItem submenuSobre = new JMenuItem("Sobre");
        submenuSobre.addActionListener((ActionEvent e) -> {
            JOptionPane.showMessageDialog(null,"Universidade Feevale\n\n" + 
                                               "Ciência da Computação\n\n" + 
                                               "Trabalho de Conclusão de Curso\n\n" + 
                                               "Aluno: Rodrigo Freiberger Rönnau\n\n" + 
                                               "Orientadora: Marta Rosecler Bez\n\n" + 
                                               "Coorientador: Jéferson Cristiano Flores", "Sobre",JOptionPane.INFORMATION_MESSAGE);
        });
        menuSobre.add(submenuSobre);
        janela.setJMenuBar(menu);
    }

    private void criaRodape() {
        painelRodape.setLayout(new GridLayout(0, 3));

        painelRodape.add(new JLabel("Nível de janela (WL)"));
        windowLevel = new JFormattedTextField();
        windowLevel.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
            }
            @Override
            public void focusLost(FocusEvent e) {
                atualizaImagem();
            }
        });
        painelRodape.add(windowLevel);

        ActionListener listenerCheckBoxes = (ActionEvent e) -> {
            atualizaImagem();
        };
        
        segPulmaoEsq = new JCheckBox("Pulmão esquerdo");
        segPulmaoEsq.addActionListener(listenerCheckBoxes);
        painelRodape.add(segPulmaoEsq);

        painelRodape.add(new JLabel("Largura de janela (WW)"));
        windowWidth = new JFormattedTextField();
        windowWidth.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
            }
            @Override
            public void focusLost(FocusEvent e) {
                atualizaImagem();
            }
        });
        painelRodape.add(windowWidth);

        segPulmaoDir = new JCheckBox("Pulmão direito");
        segPulmaoDir.addActionListener(listenerCheckBoxes);
        painelRodape.add(segPulmaoDir);
        
        painelRodape.add(new JLabel("Templates"));
        templateWLWW = new JComboBox();
        templateWLWW.addItem(LITERAL_OSSOS);
        templateWLWW.addItem(LITERAL_MEDIASTINO);
        templateWLWW.addItem(LITERAL_PULMOES);
        templateWLWW.addItem(LITERAL_CUSTOMIZADO);
        templateWLWW.setSelectedItem(LITERAL_PULMOES);
        trataTrocaTemplateWLWW();
        templateWLWW.addItemListener((ItemEvent e) -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                trataTrocaTemplateWLWW();
                atualizaImagem();
            }
        });
        painelRodape.add(templateWLWW);

    }

    private void criaInformacoes() {
        JScrollPane sPainelInfo = new JScrollPane();
        painelCentral.add(sPainelInfo, BorderLayout.EAST);

        JPanel painelInfo = new JPanel(new GridLayout(0, 1));
        sPainelInfo.setViewportView(painelInfo);

        painelInfo.add(new JLabel("                 INFORMAÇÕES                 "));
        
        labelNomeArquivo = new JLabel();
        painelInfo.add(labelNomeArquivo);

        labelIndiceImagem = new JLabel();
        painelInfo.add(labelIndiceImagem);
        
        espessuraFatia = new JLabel();
        painelInfo.add(espessuraFatia);
        
        labelCoordenadas = new JLabel();
        painelInfo.add(labelCoordenadas);
        
        labelHU = new JLabel();
        painelInfo.add(labelHU);
        
        labelRGB = new JLabel();
        painelInfo.add(labelRGB);
        
        tamPulmaoEsquerdo = new JLabel();
        painelInfo.add(tamPulmaoEsquerdo);
        
        tamPulmaoDireito = new JLabel();
        painelInfo.add(tamPulmaoDireito);

    }

    private void atualizaCoordenadas(final int x, final int y) {
        //se não tem exame carregado
        if (!controle.temExameCarregado()) {
            labelCoordenadas.setText("");
            labelHU.setText("");
            labelRGB.setText("");
            return;
        }
        
        //atualiza as infromações do pixel selecionado
        labelCoordenadas.setText("  Coordenadas: " + x + ", " + y);
        //MELHORAR ESTE PONTO, A FORMA COMO OBTEM OS DADOS!
        labelHU.setText("  Valor em HU: " + controle.dados.getMatrizOriginal(getValorSlider())[x][y]);

        BufferedImage imagem = new BufferedImage(sliceCorrente.getIcon().getIconWidth(),
                                                sliceCorrente.getIcon().getIconHeight(),
                                                BufferedImage.TYPE_INT_RGB);

        sliceCorrente.getIcon().paintIcon(null, imagem.getGraphics(), 0, 0);

        Color c = new Color(imagem.getRGB(x, y));

        labelRGB.setText("  Valor RGB: " + c.getRed() + ", " + c.getGreen() + ", " + c.getBlue() );
        
    }


    private void trataTrocaTemplateWLWW() {
        String opcaoSelecionada = (String) templateWLWW.getSelectedItem();
        switch (opcaoSelecionada) {
            case LITERAL_OSSOS:
                windowLevel.setText("300");
                windowWidth.setText("1500");
                break;
            case LITERAL_MEDIASTINO:
                windowLevel.setText("40");
                windowWidth.setText("400");
                break;
            case LITERAL_PULMOES:
                windowLevel.setText("-400");
                windowWidth.setText("1500");
                break;
        }
    }

    void atualizaImagem() {
        BufferedImage imagem = controle.geraImagem();
        sliceCorrente.setIcon(new ImageIcon(imagem));
        
        labelNomeArquivo.setText("  Arquivo: " + controle.dados.getNomeArquivo(getValorSlider()));
        labelIndiceImagem.setText("  Fatia: " + (getValorSlider() + 1) + " / " + controle.dados.getNumeroFatias());
        espessuraFatia.setText("  Espessura: " + controle.dados.getEspessuraFatia(getValorSlider()) + " mm");
        tamPulmaoEsquerdo.setText("  Pulmão esquerdo: " + controle.dados.getTamanhoPulmaoEsq(getValorSlider()) + " pixels");
        tamPulmaoDireito.setText("  Pulmão direito: " + controle.dados.getTamanhoPulmaoDir(getValorSlider()) + " pixels");
        
    }

    int getValorSlider() {
        return slider.getValue();
    }

    int getWW() {
        return Integer.parseInt(windowWidth.getText());
    }

    int getWL() {
        return Integer.parseInt(windowLevel.getText());
    }    
    
    boolean isSegPulmaoEsqMarcado() {
        return segPulmaoEsq.isSelected();
    }
    
    boolean isSegPulmaoDirMarcado() {
        return segPulmaoDir.isSelected();
    }
           
    void limitaSlider(int numeroFatias) {
        slider.setMaximum(numeroFatias - 1);
    }

}
