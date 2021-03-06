package shortest_path_visualizer.algorithms;

import shortest_path_visualizer.dataStructures.DynamicArray;
import shortest_path_visualizer.dataStructures.Keko;
import shortest_path_visualizer.dataStructures.Node;
import shortest_path_visualizer.utils.MathFunctions;

/**
 * Luokka Jump Point Search -algoritmille.
 */
public class JPS {
  /**
   * Algoritmin käyttämä kartta ascii-merkkeinä matriisimuodossa.
   */
  private char[][] kartta;
  /**
   * Sama kartta, mutta koostuu ascii-merkkien sijaan Node-olioista.
   * Mahdollistaa pääsyn Node-olioihin ja niihin tallennettuun tietoon,
   * kuten etäisyyksiin (g-matka), sekä tiedon tallentamisen Node-olioihin (uudet
   * etäisyydet, onko vierailtu solmussa jne.)
   */
  private Node[][] solmumatriisi;
  private Node goalNode;
  private Node startingNode;
  /**
   * Minimikeko, johon lisätään A*:stä ja Dijkstrasta poiketen ainoastaan ns. Jump pointit.
   */
  private Keko jumpPoints;
  private MathFunctions math;
  private boolean goalFound;
  private DynamicArray visitedNodes;

  public JPS() {

  }

  /**
   * Asettaa algoritmille tarkasteltavan kartan ja alustaa tarvittavat luokat ja muuttujat.
   * Käytetään myös tutkittavan kartan vaihtamiseen.
   *
   * @param kartta tutkittava char-muotoinen kartta
   */
  public void setMap(char[][] kartta) {
    this.kartta = kartta;
    this.solmumatriisi = new Node[kartta.length][kartta[0].length];
    this.jumpPoints = new Keko();
    this.math = new MathFunctions();
    this.goalFound = false;
    this.visitedNodes = new DynamicArray();
    initSolmumatriisi();
  }

  /**
   * Suorittaa JPS-algoritmin.
   */
  public void runJPS() {
    int x = startingNode.getX();
    int y = startingNode.getY();

    // Luodaan aloitussolmut. Kaikki lähtevät samasta pisteestä, mutta eri suuntiin.
    for (int i = -1; i < 2; i++) {
      for (int j = -1; j < 2; j++) {
        if (i == 0 && j == 0) {
        } else {
          Node n = new Node(x, y, j, i, 0);
          n.setEtaisyys(diagonalDist(n, goalNode));
          n.setAsStartNode();
          solmumatriisi[y][x] = n;
          jumpPoints.addNode(n);
        }
      }
    }
    // Dijkstran ja A*:n tapaan hakua jatketaan niin kauan kun keko ei ole tyhjä
    // tai maalisolmua ei ole löytynyt.
    while (!jumpPoints.isEmpty()) {
      Node current = jumpPoints.pollNode();
      if (!current.isStart() && !current.isGoal()) {
        visitedNodes.add(current);
      }
      if (goalFound) {
        break;
      }
      // Tarkistetaan keosta nostetun Noden hakusuunnat dirH ja dirV.
      // Näiden perusteella valitaan oikea hakusuunta/hakumetodi.
      if (current.getDirH() != 0 && current.getDirV() != 0) {
        diagonalScan(current);
      } else if (current.getDirH() != 0 && current.getDirV() == 0) {
        horizontalScan(current);
      } else if (current.getDirV() != 0 && current.getDirH() == 0) {
        verticalScan(current);
      }
    }
  }

  /**
   * Palauttaa vieraillut solmut.
   *
   * @return vieraillut solmut DynamicArray-taulukkona
   */
  public DynamicArray getVisitedNodes() {
    return this.visitedNodes;
  }

  public boolean goalWasFound() {
    return this.goalFound;
  }

  /**
   * Jäljittää reitin maalisolmusta takaisin lähtösolmuun.
   *
   * @return lista erään lyhimmän reitin käyttämistä jump point-solmuista.
   */
  public DynamicArray getReitti() {
    DynamicArray warpPoints = new DynamicArray();
    Node takaisin = goalNode.getParent();
    warpPoints.add(goalNode);
    warpPoints.add(takaisin);
    while (!takaisin.isStart()) {
      kartta[takaisin.getY()][takaisin.getX()] = 'X';
      warpPoints.add(takaisin);
      takaisin = takaisin.getParent();
    }
    warpPoints.add(startingNode);
    return warpPoints;
  }

  public Node getGoalNode() {
    return this.goalNode;
  }

  /**
   * Alustaa solmumatriisin. Luo jokaiselle char-muotoisen karttamatriisin solulle Node-olion
   * ja asettaa sen alkuetäisyyksiksi "äärettömyyden".
   * Hakee myös kartasta lähtö- ja maalisolmut ja tallentaa nämä muuttujiin
   * startingNode ja goalNode talteen.
   */
  public void initSolmumatriisi() {
    int solmutunnus = 0;
    for (int i = 0; i < kartta.length; i++) {
      for (int j = 0; j < kartta[0].length; j++) {
        Node node = new Node(solmutunnus, j, i);
        node.setG_Matka(Integer.MAX_VALUE);
        node.setEtaisyys(Integer.MAX_VALUE);

        if (kartta[i][j] == 'G') {
          node.setAsGoalNode();
          this.goalNode = node;
        } else if (kartta[i][j] == 'S') {
          node.setAsStartNode();
          node.setG_Matka(0);
          node.setEtaisyys(0);
          this.startingNode = node;
        }
        solmumatriisi[i][j] = node;
        solmutunnus++;
      }
    }
  }

  /**
   * Alkaa tutkia naapurisolmuja x-akselilla.
   * Parametrina annettavasta solmun vanhemmasta saadaan tarvittavat koordinaatti- ja suuntatiedot.
   * Jos vanhemman dirH on esimerkiksi -1, lähdetään tutkimaan viereisiä solmuja x-akselilla vasempaan
   * suuntaan. Haku jatkuu niin kauan dirH suuntaan, kunnes tulee vastaan este,
   * kartan raja tai maalisolmu. Löydetyt jump pointit lisätään kekoon.
   *
   * @param parent Solmun vanhempi
   */
  public void horizontalScan(Node parent) {
    int x = parent.getX();
    int y = parent.getY();
    int dirH = parent.getDirH();
    double distance = parent.getG_Matka();
    int dx = x;

    while (true) {
      if (dirH == 0) {
        break;
      }
      dx += dirH;

      if (dx < 0 || dx > kartta[0].length - 1) {
        break;
      }
      if (kartta[y][dx] == '@') {
        break;
      }
      distance += 1;
      int x2 = dx + dirH;

      if (kartta[y][dx] == 'G') {
        goalFound = true;
        goalNode.setParent(parent);
        goalNode.setG_Matka(distance);
        break;
      }
      Node node = solmumatriisi[y][dx];
      // Tarkistaa onko tarvetta päivittää solmun g-matkaa paremmaksi
      if (!node.onVierailtu() || node.getG_Matka() > distance) {
        // Varmistaa ettei kartan raja ylity
        if (x2 >= 0 && x2 < kartta[0].length && y - 1 >= 0) {
          // Katsoo onko solmu sijainnissa (dx, y) Jump Point. Jos on se lisätään kekoon.
          if (kartta[y - 1][dx] == '@' && kartta[y - 1][x2] != '@') {
            node = new Node(dx, y, dirH, -1, distance);
            node.setEtaisyys(distance + diagonalDist(node, goalNode));
            node.vieraile();
            node.setParent(parent);
            solmumatriisi[y][dx] = node;
            jumpPoints.addNode(node);
          }
        }
        // Varmistaa ettei kartan raja ylity
        if (x2 >= 0 && x2 < kartta[0].length && y + 1 < kartta.length) {
          // Katsoo onko solmu sijainnissa (dx, y) Jump Point.
          // Tarkistus tehdään tässä siis tavallaan "uudestaan", sillä
          // yllä oleva tarkistus tarkisti solmun ympäriltä eri solmut kuin
          // tämä jälkimmäinen tarkistus.
          if (kartta[y + 1][dx] == '@' && kartta[y + 1][x2] != '@') {
            node = new Node(dx, y, dirH, 1, distance);
            node.setParent(parent);
            node.setEtaisyys(distance + diagonalDist(node, goalNode));
            node.vieraile();
            solmumatriisi[y][dx] = node;
            jumpPoints.addNode(node);
          }
        }
      }
    }
  }

  /**
   * Sama idea kuin metodissa horizontalScan, mutta nyt haetaan hyppypisteitä y-akselilta.
   *
   * @param parent Solmun vanhempi
   */
  public void verticalScan(Node parent) {
    int x = parent.getX();
    int y = parent.getY();
    int dirV = parent.getDirV();
    double distance = parent.getG_Matka();
    int dy = y;

    while (true) {
      if (dirV == 0) {
        break;
      }
      dy += dirV;
      if (dy < 0 || dy > kartta[0].length - 1) {
        break;
      }
      if (kartta[dy][x] == '@') {
        break;
      }
      distance += 1;
      int y2 = dy + dirV;

      if (kartta[dy][x] == 'G') {
        goalFound = true;
        goalNode.setParent(parent);
        goalNode.setG_Matka(distance);
        break;
      }

      // Vastaavat tarkastukset Jump Pointtien varalta, kuten horizontalScnnissa,
      // mutta nyt vertikaalisessa suunnassa.
      Node node = solmumatriisi[dy][x];
      if (!node.onVierailtu() || node.getG_Matka() > distance) {
        if (y2 >= 0 && y2 < kartta.length && x - 1 >= 0) {
          if (kartta[dy][x - 1] == '@' && kartta[y2][x - 1] != '@') {
            node = new Node(x, dy, -1, dirV, distance);
            node.setEtaisyys(distance + diagonalDist(node, goalNode));
            node.vieraile();
            node.setParent(parent);
            solmumatriisi[dy][x] = node;
            jumpPoints.addNode(node);
          }
        }
        if (y2 >= 0 && y2 < kartta.length && x + 1 < kartta[0].length) {
          if (kartta[dy][x + 1] == '@' && kartta[y2][x + 1] != '@') {
            node = new Node(x, dy, 1, dirV, distance);
            node.setEtaisyys(distance + diagonalDist(node, goalNode));
            node.vieraile();
            node.setParent(parent);
            solmumatriisi[dy][x] = node;
            jumpPoints.addNode(node);
          }
        }
      }
    }
  }

  /**
   * Jump pointtien hakeminen vinottain. Tähän tarvitaan sekä dirH, että dirV-arvot,
   * eli tutkittavat suunnat x- ja y-akselilla. Jokaista yhtä diagonaalista askelta kohti
   * suoritetaan horizontalScan ja verticalScan. Tämän jälkeen edetään taas
   * yksi askel diagonaalisesti. Etenemistä jatketaan taas kunnes vastaan tulee este,
   * kartan raja tai maalisolmu.
   *
   * @param parent Solmun vanhempi
   */
  public void diagonalScan(Node parent) {
    int x = parent.getX();
    int y = parent.getY();
    int dirH = parent.getDirH();
    int dirV = parent.getDirV();
    double distance = parent.getG_Matka();
    int yNext = y;
    int xNext = x;

    while (true) {
      xNext += dirH;
      yNext += dirV;
      distance += Math.sqrt(2);

      if (xNext > kartta[0].length - 1 || xNext < 0 || yNext > kartta.length - 1 || yNext < 0) {
        break;
      }
      if (kartta[yNext][xNext] == '@') {
        break;
      }
      if (kartta[yNext][xNext] == 'G') {
        goalFound = true;
        goalNode.setG_Matka(distance);
        goalNode.setParent(parent);
        break;
      }
      // Luo uuden Node-olion vanhemmaksi horizontalScannia varten.
      // Uusi olio siksi, että sille saadaan päivitettyä tarvittavat arvot, kuten matka, suunta jne.
      Node newParent = new Node(xNext, yNext, dirH, 0, distance);
      newParent.setParent(parent);
      horizontalScan(newParent);

      // Luo uuden Node olion vanhemmaksi verticalScannia varten.
      newParent = new Node(xNext, yNext, 0, dirV, distance);
      newParent.setParent(parent);
      verticalScan(newParent);

      // Vastaavat tarkistukset Jump Pointien varalle, kuin horizontalScan
      // ja verticalScan-metodeissa, mutta diagonaalisessa suunnassa.
      Node node = solmumatriisi[yNext][xNext];
      if (!node.onVierailtu() || node.getG_Matka() > distance) {
        if (xNext - 1 >= 0 && xNext + 1 < kartta[0].length && yNext - 1 >= 0
            && yNext + 1 < kartta.length) {
          if (kartta[yNext][xNext + (-1) * dirH] == '@'
              && kartta[yNext + dirV][xNext + (-1) * dirH] != '@') {
            node = new Node(xNext, yNext, -(dirH), dirV, distance);
            node.setEtaisyys(distance + diagonalDist(node, goalNode));
            node.vieraile();
            node.setParent(parent);
            solmumatriisi[yNext][xNext] = node;
            jumpPoints.addNode(node);
          }
        }
        if (yNext - 1 >= 0 && yNext + 1 < kartta.length && xNext - 1 >= 0
            && xNext + 1 < kartta[0].length) {
          if (kartta[yNext + (-1) * dirV][xNext] == '@'
              && kartta[yNext + (-1) * dirV][xNext + dirH] != '@') {
            node = new Node(xNext, yNext, dirH, -(dirV), distance);
            node.setEtaisyys(distance + diagonalDist(node, goalNode));
            node.vieraile();
            node.setParent(parent);
            solmumatriisi[yNext][xNext] = node;
            jumpPoints.addNode(node);
          }
        }
      }
    }
  }

  /**
   * Heuristiikka, joka laskee arvioidun etäisyyden tutkittavan solmun
   * ja maalisolmun välille, kun ruudukko sallii liikkumisen
   * vinoittain ruutujen välillä.
   *
   * @param n1 Ensimmäinen solmu
   * @param n2 Toinen solmu
   * @return Arvioitu etäisyys solmusta n1 solmuun n2, solmu n2 aina maalisolmu.
   */
  public double diagonalDist(Node n1, Node n2) {
    double dx = math.getAbs(n1.getX() - n2.getX());
    double dy = math.getAbs(n1.getY() - n2.getY());
    return (dx + dy) + (Math.sqrt(2) - 2) * math.getMin(dx, dy);
  }

}











