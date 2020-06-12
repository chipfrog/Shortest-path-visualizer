package shortest_path_visualizer.performanceTesting;

import java.io.File;
import java.io.FileNotFoundException;
import shortest_path_visualizer.IO.MapReader;
import shortest_path_visualizer.IO.MapReaderIO;
import shortest_path_visualizer.algorithms.AStar;
import shortest_path_visualizer.algorithms.Dijkstra;

public class PerformanceTest {
  MapReaderIO io = new MapReaderIO();
  MapReader mapReader = new MapReader(io);
  private Dijkstra dijkstra;
  private AStar aStar;
  private char[][] map;
  private long[] times;
  private File file;
  private int vastausD;
  private int vastausA;

  public PerformanceTest() {
    this.io = new MapReaderIO();
    this.mapReader = new MapReader(io);
    this.times = new long[100];
    this.file = new File("src/test/resources/kartat/Brushfire.txt");

  }

  public char[][] initMap() throws FileNotFoundException {
    mapReader.createMatrix(file);
    return mapReader.getMapArray();
  }



  public void testDijkstra() throws FileNotFoundException {
    mapReader.createMatrix(file);
    this.map = mapReader.getMapArray();
    this.dijkstra = new Dijkstra(new MapReaderIO(), this.map);

    dijkstra.runDijkstra();
    this.vastausD = dijkstra.getEtaisyysMaaliin();

    for (int i = 0; i < 100; i ++) {
      long t1 = System.nanoTime();
      this.dijkstra = new Dijkstra(new MapReaderIO(), this.map);
      dijkstra.runDijkstra();
      long t2 = System.nanoTime();
      times[i] = t2 - t1;
    }
  }

  public void testAStar() throws FileNotFoundException {
    mapReader.createMatrix(file);
    this.map = mapReader.getMapArray();
    this.aStar = new AStar(new MapReaderIO(), this.map);
    aStar.runAStar();
    this.vastausA = aStar.getEtaisyysMaaliin();

    for (int i = 0; i < 100; i ++) {
      long t1 = System.nanoTime();
      this.aStar = new AStar(new MapReaderIO(), this.map);
      aStar.runAStar();
      long t2 = System.nanoTime();
      times[i] = t2 - t1;
    }
  }

  public long getAverage() {
    long totalTime = 0;
    long divider = 0;
    for (int i = 1; i < times.length; i ++) {
      totalTime += times[i];
      divider ++;
    }
    System.out.println(totalTime/1000000.0/divider);
    return totalTime/divider;
  }

  public boolean samaVastaus() {
    return vastausA == vastausD;
  }

  public int getVastausA() {
    return this.vastausA;
  }

  public int getVastausD() {
    return this.vastausD;
  }
}