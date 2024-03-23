import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {

        CityGraph cityGraph = new CityGraph(4);
        addConnections(cityGraph);

        File f = new File("query.inp");
        Scanner scan = new Scanner(f);
        String queryType;
        String startCity = "";
        String destination = "";
        int highwayCount = 0;
        int airwayCount = 0;
        int railwayCount = 0;
        int stopCount = 0;
        String transportation;
        int transportationType = 0;

        while(scan.hasNext()){
            queryType = scan.next();
            startCity = scan.next();
            destination = scan.next();
            if(queryType.equals("Q1")){
                highwayCount = scan.nextInt();
                airwayCount = scan.nextInt();
                railwayCount = scan.nextInt();
            }
            else if(queryType.equals("Q2")){
                stopCount = scan.nextInt();
            }
            else if(queryType.equals("Q3")){
                transportation = scan.next();
                if(transportation.equals("H")){
                    transportationType = 1;
                }
                else if(transportation.equals("A")){
                    transportationType = 2;
                }
                else if(transportation.equals("R")){
                    transportationType = 3;
                }
            }
        }
        scan.close();

        cityGraph.query1(startCity, destination, highwayCount, airwayCount, railwayCount);
        cityGraph.query2(startCity, destination, stopCount);
        cityGraph.query3(startCity, destination, transportationType);
    }

    public static void addConnections(CityGraph cityGraph){
        cityGraph.addCity("Istanbul");
        cityGraph.addCity("Ankara");
        cityGraph.addCity("Izmir");
        cityGraph.addCity("Erzurum");

        cityGraph.addConnection("Erzurum", "Istanbul", 1);
        cityGraph.addConnection("Ankara", "Izmir", 1);
        cityGraph.addConnection("Izmir", "Erzurum", 1);

        cityGraph.addConnection("Istanbul", "Ankara", 2);
        cityGraph.addConnection("Ankara", "Izmir", 2);
        cityGraph.addConnection("Izmir", "Erzurum", 2);

        cityGraph.addConnection("Istanbul", "Ankara", 3);
        cityGraph.addConnection("Ankara", "Erzurum", 3);
        cityGraph.addConnection("Izmir", "Erzurum", 3);
    }
}