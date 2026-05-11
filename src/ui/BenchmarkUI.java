package ui;

import benchmark.BenchmarkResult;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;

/**
 * Fenetre Swing affichant les resultats du benchmark.
 *
 * Organisation :
 *  - Un onglet par scenario
 *  - Dans chaque onglet : un graphique en barres par operation
 *    (ajout, recherche, suppression, inventaire, comptage, total)
 *  - Chaque graphique compare LinkedList (rouge) vs HashMap (bleu)
 *    pour toutes les tailles testees
 */
public class BenchmarkUI extends JFrame {

    // ── Couleurs ──────────────────────────────────────────────────────────────
    private static final Color COL_LL      = new Color(220, 60,  60);   // rouge LinkedList
    private static final Color COL_HM      = new Color(37, 99, 235);    // bleu HashMap
    private static final Color COL_BG      = new Color(15,  23,  42);   // fond sombre
    private static final Color COL_PANEL   = new Color(30,  41,  59);   // fond panneau
    private static final Color COL_GRID    = new Color(51,  65,  85);   // grille
    private static final Color COL_TEXT    = new Color(226, 232, 240);  // texte clair
    private static final Color COL_SUBTEXT = new Color(100, 116, 139);  // texte secondaire
    private static final Color COL_TITLE   = new Color(96, 165, 250);   // titre bleu clair

    // ── Opérations à afficher ─────────────────────────────────────────────────
    private static final String[] OPS = {
        "Ajout", "Recherche", "Suppression", "Inventaire", "Comptage", "Total"
    };

    public BenchmarkUI(List<BenchmarkResult> results) {
        super("HAI822I — ParcCapteurs — Benchmark");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 750);
        setLocationRelativeTo(null);
        getContentPane().setBackground(COL_BG);

        // Regrouper les résultats par scénario
        Map<String, List<BenchmarkResult>> byScenario = new LinkedHashMap<>();
        for (BenchmarkResult r : results) {
            byScenario.computeIfAbsent(r.scenario(), k -> new ArrayList<>()).add(r);
        }

        // Récupérer les tailles distinctes (dans l'ordre)
        List<Integer> tailles = new ArrayList<>();
        for (BenchmarkResult r : results) {
            if (r.structure().equals("LinkedList") && !tailles.contains(r.taille()))
                tailles.add(r.taille());
        }

        // Onglets — un par scénario
        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(COL_BG);
        tabs.setForeground(COL_TEXT);
        tabs.setFont(new Font("Arial", Font.BOLD, 12));

        for (Map.Entry<String, List<BenchmarkResult>> entry : byScenario.entrySet()) {
            String scenario = entry.getKey();
            List<BenchmarkResult> scResults = entry.getValue();
            tabs.addTab(scenario, buildScenarioPanel(scenario, scResults, tailles));
        }

        // Header
        JLabel header = new JLabel("ParcCapteurs — LinkedList vs HashMap", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 18));
        header.setForeground(COL_TITLE);
        header.setBorder(new EmptyBorder(12, 0, 8, 0));
        header.setBackground(COL_BG);
        header.setOpaque(true);

        // Légende globale
        JPanel legend = buildLegend();

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(COL_BG);
        top.add(header, BorderLayout.CENTER);
        top.add(legend, BorderLayout.EAST);

        add(top, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);

        setVisible(true);
    }

    // ── Panneau d'un scénario ─────────────────────────────────────────────────

    private JPanel buildScenarioPanel(String scenario,
                                      List<BenchmarkResult> results,
                                      List<Integer> tailles) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COL_BG);

        // Titre du scénario
        JLabel title = new JLabel(scenario, SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 14));
        title.setForeground(COL_TITLE);
        title.setBorder(new EmptyBorder(8, 0, 8, 0));
        title.setBackground(COL_BG);
        title.setOpaque(true);
        panel.add(title, BorderLayout.NORTH);

        // Grille de graphiques : 2 lignes x 3 colonnes (6 opérations)
        JPanel grid = new JPanel(new GridLayout(2, 3, 10, 10));
        grid.setBackground(COL_BG);
        grid.setBorder(new EmptyBorder(6, 10, 10, 10));

        for (String op : OPS) {
            // Extraire les données LL et HM pour cette opération
            double[] llVals = new double[tailles.size()];
            double[] hmVals = new double[tailles.size()];
            for (int i = 0; i < tailles.size(); i++) {
                int t = tailles.get(i);
                llVals[i] = getVal(results, "LinkedList", t, op);
                hmVals[i] = getVal(results, "HashMap",    t, op);
            }
            String[] labels = tailles.stream().map(String::valueOf).toArray(String[]::new);
            grid.add(new BarChartPanel(op, labels, llVals, hmVals));
        }

        panel.add(grid, BorderLayout.CENTER);
        return panel;
    }

    // ── Extraction de la valeur selon l'opération ─────────────────────────────

    private double getVal(List<BenchmarkResult> results, String struct, int taille, String op) {
        for (BenchmarkResult r : results) {
            if (r.structure().equals(struct) && r.taille() == taille) {
                switch (op) {
                    case "Ajout"       : return r.tempsAjoutNs()       / 1_000_000.0;
                    case "Recherche"   : return r.tempsRechercheNs()   / 1_000_000.0;
                    case "Suppression" : return r.tempsSuppressionNs() / 1_000_000.0;
                    case "Inventaire"  : return r.tempsInventaireNs()  / 1_000_000.0;
                    case "Comptage"    : return r.tempsComptageNs()    / 1_000_000.0;
                    case "Total"       : return r.tempsTotalNs()       / 1_000_000.0;
                }
            }
        }
        return 0;
    }

    // ── Légende ───────────────────────────────────────────────────────────────

    private JPanel buildLegend() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 8));
        p.setBackground(COL_BG);
        p.add(legendItem("LinkedList", COL_LL));
        p.add(legendItem("HashMap",    COL_HM));
        return p;
    }

    private JPanel legendItem(String label, Color color) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        p.setBackground(COL_BG);
        JLabel box = new JLabel("  ");
        box.setOpaque(true);
        box.setBackground(color);
        box.setPreferredSize(new Dimension(18, 14));
        JLabel txt = new JLabel(label);
        txt.setForeground(COL_TEXT);
        txt.setFont(new Font("Arial", Font.PLAIN, 12));
        p.add(box);
        p.add(txt);
        return p;
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Composant graphique en barres
    // ═════════════════════════════════════════════════════════════════════════

    private static class BarChartPanel extends JPanel {

        private final String   title;
        private final String[] labels;   // tailles (ex. "100", "500", ...)
        private final double[] llVals;
        private final double[] hmVals;

        private static final int PAD_L = 55;  // marge gauche (axe Y)
        private static final int PAD_R = 12;
        private static final int PAD_T = 30;  // marge haut (titre)
        private static final int PAD_B = 40;  // marge bas (labels X)

        BarChartPanel(String title, String[] labels, double[] llVals, double[] hmVals) {
            this.title  = title;
            this.labels = labels;
            this.llVals = llVals;
            this.hmVals = hmVals;
            setBackground(COL_PANEL);
            setBorder(BorderFactory.createLineBorder(COL_GRID, 1));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int W = getWidth();
            int H = getHeight();
            int chartW = W - PAD_L - PAD_R;
            int chartH = H - PAD_T - PAD_B;

            // ── Titre ─────────────────────────────────────────────────────────
            g2.setFont(new Font("Arial", Font.BOLD, 12));
            g2.setColor(COL_TITLE);
            FontMetrics fmT = g2.getFontMetrics();
            g2.drawString(title, (W - fmT.stringWidth(title)) / 2, PAD_T - 8);

            // ── Fond zone graphique ────────────────────────────────────────────
            g2.setColor(COL_BG);
            g2.fillRect(PAD_L, PAD_T, chartW, chartH);

            // ── Calcul du max ──────────────────────────────────────────────────
            double maxVal = 0.001;
            for (double v : llVals) maxVal = Math.max(maxVal, v);
            for (double v : hmVals) maxVal = Math.max(maxVal, v);
            double scale = niceMax(maxVal);

            // ── Grille horizontale ────────────────────────────────────────────
            int NB_GRID = 5;
            g2.setFont(new Font("Arial", Font.PLAIN, 9));
            FontMetrics fmY = g2.getFontMetrics();
            for (int i = 0; i <= NB_GRID; i++) {
                double val = scale * i / NB_GRID;
                int y = PAD_T + chartH - (int)(chartH * val / scale);
                g2.setColor(COL_GRID);
                g2.drawLine(PAD_L, y, PAD_L + chartW, y);
                g2.setColor(COL_SUBTEXT);
                String lbl = formatVal(val);
                g2.drawString(lbl, PAD_L - fmY.stringWidth(lbl) - 4, y + fmY.getAscent() / 2);
            }

            // ── Axe Y label ───────────────────────────────────────────────────
            g2.setColor(COL_SUBTEXT);
            g2.setFont(new Font("Arial", Font.PLAIN, 9));
            Graphics2D g2r = (Graphics2D) g2.create();
            g2r.rotate(-Math.PI / 2, 10, PAD_T + chartH / 2);
            g2r.drawString("ms", 10 - 8, PAD_T + chartH / 2);
            g2r.dispose();

            // ── Barres ────────────────────────────────────────────────────────
            int n = labels.length;
            int groupW = chartW / n;
            int barW = Math.max(4, groupW / 3);
            int gap  = Math.max(2, barW / 4);

            g2.setFont(new Font("Arial", Font.PLAIN, 9));
            FontMetrics fmX = g2.getFontMetrics();

            for (int i = 0; i < n; i++) {
                int gx = PAD_L + i * groupW + groupW / 2;

                // Barre LinkedList
                int llH = (int)(chartH * Math.min(llVals[i], scale) / scale);
                int llX = gx - barW - gap / 2;
                g2.setColor(COL_LL);
                g2.fillRect(llX, PAD_T + chartH - llH, barW, llH);

                // Valeur au dessus de la barre LL
                if (llVals[i] > 0) {
                    g2.setColor(COL_LL);
                    String v = formatVal(llVals[i]);
                    int vx = llX + (barW - fmX.stringWidth(v)) / 2;
                    int vy = PAD_T + chartH - llH - 2;
                    if (vy > PAD_T + 10) g2.drawString(v, vx, vy);
                }

                // Barre HashMap
                int hmH = (int)(chartH * Math.min(hmVals[i], scale) / scale);
                int hmX = gx + gap / 2;
                g2.setColor(COL_HM);
                g2.fillRect(hmX, PAD_T + chartH - hmH, barW, hmH);

                // Valeur au dessus de la barre HM
                if (hmVals[i] > 0) {
                    g2.setColor(COL_HM);
                    String v = formatVal(hmVals[i]);
                    int vx = hmX + (barW - fmX.stringWidth(v)) / 2;
                    int vy = PAD_T + chartH - hmH - 2;
                    if (vy > PAD_T + 10) g2.drawString(v, vx, vy);
                }

                // Label X (taille)
                g2.setColor(COL_SUBTEXT);
                String lbl = labels[i];
                int lx = gx - fmX.stringWidth(lbl) / 2;
                g2.drawString(lbl, lx, PAD_T + chartH + 14);
            }

            // ── Axe X ─────────────────────────────────────────────────────────
            g2.setColor(COL_GRID);
            g2.drawLine(PAD_L, PAD_T + chartH, PAD_L + chartW, PAD_T + chartH);
            g2.drawLine(PAD_L, PAD_T, PAD_L, PAD_T + chartH);

            // ── Label "n (capteurs)" ───────────────────────────────────────────
            g2.setColor(COL_SUBTEXT);
            g2.setFont(new Font("Arial", Font.PLAIN, 9));
            String xLabel = "n (capteurs)";
            g2.drawString(xLabel, PAD_L + (chartW - fmX.stringWidth(xLabel)) / 2,
                          H - 4);
        }

        /** Arrondit le max pour un axe propre. */
        private double niceMax(double max) {
            if (max <= 0) return 1;
            double exp = Math.pow(10, Math.floor(Math.log10(max)));
            double f   = max / exp;
            if (f <= 1)   return exp;
            if (f <= 2)   return 2 * exp;
            if (f <= 5)   return 5 * exp;
            return 10 * exp;
        }

        /** Formate une valeur ms de façon compacte. */
        private String formatVal(double v) {
            if (v >= 1000) return String.format("%.0fs", v / 1000);
            if (v >= 100)  return String.format("%.0f", v);
            if (v >= 10)   return String.format("%.1f", v);
            if (v >= 1)    return String.format("%.2f", v);
            return String.format("%.3f", v);
        }
    }
}