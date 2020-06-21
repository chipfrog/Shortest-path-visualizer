package shortest_path_visualizer.algorithms;

import java.sql.SQLOutput;
import java.util.ArrayList;
import shortest_path_visualizer.dataStructures.DynamicArray;
import shortest_path_visualizer.dataStructures.Keko;
import shortest_path_visualizer.utils.MathFunctions;
import shortest_path_visualizer.utils.NeighbourFinder;
import shortest_path_visualizer.utils.Node;

public class JPS {
  private char[][] kartta;
  private Node[][] solmumatriisi;
  private Node goalNode;
  private Node startingNode;
  private Keko jumpPoints;
  private MathFunctions math;
  private boolean goalFound;
  private DynamicArray visitedNodes;

  public JPS() {

  }

  public void setMap(char[][] kartta) {
    this.kartta = kartta;
    this.solmumatriisi = new Node[kartta.length][kartta[0].length];
    this.jumpPoints = new Keko();
    this.math = new MathFunctions();
    this.goalFound = false;
    this.visitedNodes = new DynamicArray();
    initSolmumatriisi();
  }

  public void runJPS() {
    int x = startingNode.getX();
    int y = startingNode.getY();

    // Luodaan aloitussolmut. Kaikki lähtevät samasta pisteestä, mutta eri suuntiin.
    for (int i = -1; i < 2; i ++) {
      for (int j = -1; j < 2; j++ ) {
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
    while (!jumpPoints.isEmpty()) {
      Node current = jumpPoints.pollNode();
      //System.out.println("X: " + current.getX() + "Y: " + current.getY());
      //System.out.println("DirH: " + current.getDirH() + ", DirV: " + current.getDirV());
      if (!current.isStart()) {
        visitedNodes.add(current);
      }

      if (goalFound) {
        //System.out.println("Maali löytyi!");
        break;
      }
      // Tämä voi olla ongelmallinen ehto!
      if (current.getDirH() != 0 && current.getDirV() != 0) {
        diagonalScan(current);
      } else if (current.getDirH() != 0 && current.getDirV() == 0) {
        horizontalScan(current);
      } else if (current.getDirV() != 0 && current.getDirH() == 0) {
        verticalScan(current);
      }
    }
    //System.out.println(visitedNodes.size());
    if (!goalFound) {
      //System.out.println("Ei löytynyt");
      //System.out.println("Visited nodes: " + visitedNodes.size());
    }
  }
  public DynamicArray getVisitedNodes() {
    return this.visitedNodes;
  }

  public Node getGoalNode() {
    return this.goalNode;
  }

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
        //System.out.println("Maalietäisyys: " + distance);
        goalNode.setG_Matka(distance);
        break;
      }

      if (x2 >= 0 && x2 < kartta[0].length && y - 1 >= 0) {
        if (kartta[y - 1][dx] == '@' && kartta[y - 1][x2] != '@') {
          Node node = new Node(dx, y, dirH, -1, distance);
          //Node node = new Node(x2, y - 1, dirH, -1, distance + Math.sqrt(2));
          node.setEtaisyys(distance + diagonalDist(node, goalNode));
          jumpPoints.addNode(node);
        }
      }
      if (x2 >= 0 && x2 < kartta[0].length && y + 1 < kartta.length) {
        if (kartta[y + 1][dx] == '@' && kartta[y + 1][x2] != '@') {
          Node node = new Node(dx, y, dirH, 1, distance);
          //Node node = new Node(x2, y + 1, dirH, 1, distance + Math.sqrt(2));
          node.setEtaisyys(distance + diagonalDist(node, goalNode));
          jumpPoints.addNode(node);
        }
      }
    }
  }

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
        //System.out.println("Maalietäisyys: " + distance);
        goalNode.setG_Matka(distance);
        break;
      }
      // Ehdot erikseen?
      if (y2 >= 0 && y2 < kartta.length && x - 1 >= 0) {
        if (kartta[dy][x - 1] == '@' && kartta[y2][x - 1] != '@') {
          Node node = new Node(x, dy, -1, dirV, distance);
          //Node node = new Node(x - 1, y2, -1, dirV, distance + Math.sqrt(2));
          //System.out.println(node.getX()+ ";" + node.getY());
          node.setEtaisyys(distance + diagonalDist(node, goalNode));
          jumpPoints.addNode(node);
        }
      }
      if (y2 >= 0 && y2 < kartta.length && x + 1 < kartta[0].length) {
        if (kartta[dy][x + 1] == '@' && kartta[y2][x + 1] != '@') {
          Node node = new Node(x, dy, 1, dirV, distance);
          //Node node = new Node(x + 1, y2, 1, dirV, distance + Math.sqrt(2));
          node.setEtaisyys(distance + diagonalDist(node, goalNode));
          jumpPoints.addNode(node);
        }
      }
      //dy += dirV;
    }
  }

  public void diagonalScan(Node parent) {
    int x = parent.getX();
    int y = parent.getY();
    int dirH = parent.getDirH();
    int dirV = parent.getDirV();
    double distance = parent.getG_Matka();
    int yNext = y;
    int xNext = x;
     // TÄSSÄ VIRHE
    // EI SAA ALUSSA PÄIVITTÄÄ xNEXT ja yNEXT
    while (true) {
      xNext += dirH;
      yNext += dirV;
      distance += Math.sqrt(2);

      if (xNext > kartta[0].length - 1|| xNext < 0 || yNext > kartta.length - 1 || yNext < 0) {
        break;
      }
      if (kartta[yNext][xNext] == '@') {
        break;
      }
      if(kartta[yNext][xNext] == 'G') {
        goalFound = true;
        goalNode.setG_Matka(distance);
        //System.out.println("Maalietäisyys: " + distance);
        break;
      }
      // tähän ehkä kaksi eri vanhempaa dirH/dirV suhteen
      // ja ehdot rajojen ylityksen varalle
      Node newParent = new Node(xNext, yNext, dirH, 0, distance);
      horizontalScan(newParent);
      newParent = new Node(xNext, yNext, 0, dirV, distance);
      verticalScan(newParent);

      /*xNext += dirH;
      yNext += dirV;
      distance += Math.sqrt(2);*/

      if (xNext - 1 >= 0 && xNext + 1 < kartta[0].length && yNext - 1 >= 0 && yNext + 1 < kartta.length) {
        if (kartta[yNext][xNext + (-1) * dirH] == '@' && kartta[yNext + dirV][xNext + (-1) * dirH] != '@') {
          Node node = new Node(xNext, yNext, -(dirH), dirV, distance);
          node.setEtaisyys(distance + diagonalDist(node, goalNode));
          jumpPoints.addNode(node);
        }
      }
      if (yNext - 1 >= 0 && yNext + 1 < kartta.length && xNext - 1 >= 0 && xNext + 1 < kartta[0].length) {
        if (kartta[yNext + (-1) * dirV][xNext] == '@' && kartta[yNext + (-1) * dirV][xNext + dirH] != '@') {
          Node node = new Node(xNext, yNext, dirH, -(dirV), distance);
          node.setEtaisyys(distance + diagonalDist(node, goalNode));
          jumpPoints.addNode(node);
        }
      }
    }
  }

  public double diagonalDist(Node n1, Node n2) {
    double dx = math.getAbs(n1.getX() - n2.getX());
    double dy = math.getAbs(n1.getY() - n2.getY());
    return (dx + dy) + (Math.sqrt(2) - 2) * math.getMin(dx, dy);
  }

}











