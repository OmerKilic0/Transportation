import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class CityGraph {
    private int numCities;
    private Map<String, Integer> cityIndexMap;
    private List<String> cities;
    public List<List<Integer>> highwayMatrix;
    public List<List<Integer>> airwayMatrix;
    public List<List<Integer>> railwayMatrix;
    private List<List<Integer>> paths;

    public CityGraph(int numCities) {
        this.numCities = numCities;
        cityIndexMap = new HashMap<>();
        cities = new ArrayList<>();
        highwayMatrix = new ArrayList<>();
        airwayMatrix = new ArrayList<>();
        railwayMatrix = new ArrayList<>();
        paths = new ArrayList<>();

        for (int i = 0; i < numCities; i++) {
            List<Integer> row = new ArrayList<>();
            for (int j = 0; j < numCities; j++) {
                row.add(0);
            }
            highwayMatrix.add(row);
            airwayMatrix.add(row);
            railwayMatrix.add(row);
        }
    }

    public void addCity(String cityName) {
        cityIndexMap.put(cityName, cities.size());
        cities.add(cityName);
    }

    public void addConnection(String city1, String city2, int transportationType) {
        int index1 = cityIndexMap.get(city1);
        int index2 = cityIndexMap.get(city2);

        int connectionValue = (transportationType > 0) ? 1 : 0;

        highwayMatrix.get(index1).set(index2, connectionValue);
        highwayMatrix.get(index2).set(index1, connectionValue);

        airwayMatrix.get(index1).set(index2, connectionValue);
        airwayMatrix.get(index2).set(index1, connectionValue);

        railwayMatrix.get(index1).set(index2, connectionValue);
        railwayMatrix.get(index2).set(index1, connectionValue);
    }

    public void printMatrix(List<List<Integer>> matrix, String matrixType) {
        System.out.println(matrixType);
        System.out.print("Cities\t");
        for (String city : cities) {
            System.out.print(city + "\t");
        }
        System.out.println();

        for (int i = 0; i < numCities; i++) {
            System.out.print(cities.get(i) + "\t");
            for (int j = 0; j < numCities; j++) {
                System.out.print(matrix.get(i).get(j) + "\t");
            }
            System.out.println();
        }
        System.out.println();
    }

    public void query1(String startCity, String destination, int highwayCount, int airwayCount, int railwayCount) throws IOException {
        int startCityIndex = cityIndexMap.get(startCity);
        int destinationIndex = cityIndexMap.get(destination);

        List<Integer> currentPath = new ArrayList<>();
        boolean[] visitedCities = new boolean[cities.size()];

        query1(startCityIndex, destinationIndex, highwayCount, airwayCount, railwayCount, currentPath, visitedCities);

        if(!paths.isEmpty()){
            writeOutputQuery1(paths.get(0), highwayCount, airwayCount, railwayCount, startCity, destination);
        }
    }

    private void query1(int currentCityIndex, int destinationIndex, int highwayCount, int airwayCount, int railwayCount,
                        List<Integer> currentPath, boolean[] visitedCities) {
        visitedCities[currentCityIndex] = true;
        currentPath.add(currentCityIndex);

        if (currentCityIndex == destinationIndex &&
                countTransportationTypes(currentPath) == highwayCount + airwayCount + railwayCount) {
            paths.add(new ArrayList<>(currentPath));
            visitedCities[currentCityIndex] = false;
            currentPath.remove(currentPath.size() - 1);
            return;
        }

        for (int neighborIndex = 0; neighborIndex < cities.size(); neighborIndex++) {
            if (!visitedCities[neighborIndex]) {
                query1(neighborIndex, destinationIndex, highwayCount, airwayCount, railwayCount, currentPath, visitedCities);
            }
        }

        visitedCities[currentCityIndex] = false;
        currentPath.remove(currentPath.size() - 1);
    }

    private void writeOutputQuery1(List<Integer> path, int highwayCount, int airwayCount, int railwayCount, String startCity, String destination) throws IOException {
        File file = new File("out1.txt");
        FileWriter fileWriter = new FileWriter(file);
        BufferedWriter buffW = new BufferedWriter(fileWriter);

        buffW.write("[Query 1] Path from " + startCity + " to " + destination +
                " with counts: Highway=" + highwayCount + ", Airway=" + airwayCount + ", Railway=" + railwayCount + "\n");

        if (path.isEmpty()) {
            buffW.write("No valid path found.");
        } else {
            int currentCityIndex;
            int nextCityIndex = 0;
            int currentTransportationType;

            for (int i = 0; i < path.size() - 1; i++) {
                currentCityIndex = path.get(i);
                nextCityIndex = path.get(i + 1);
                currentTransportationType = getTransportationTypeQ1(currentCityIndex, nextCityIndex, highwayCount, airwayCount, railwayCount);

                if (currentTransportationType == 1) {
                    buffW.write(cities.get(currentCityIndex) + " H ");
                    highwayCount--;
                } else if (currentTransportationType == 2) {
                    buffW.write(cities.get(currentCityIndex) + " A ");
                    airwayCount--;
                } else if (currentTransportationType == 3) {
                    buffW.write(cities.get(currentCityIndex) + " R ");
                    railwayCount--;
                }
            }
            buffW.write(cities.get(nextCityIndex));
        }
        buffW.close();
    }

    private int countTransportationTypes(List<Integer> path) {
        int count = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            int currentCityIndex = path.get(i);
            int nextCityIndex = path.get(i + 1);

            if (highwayMatrix.get(currentCityIndex).get(nextCityIndex) == 1) {
                count++;
            } else if (airwayMatrix.get(currentCityIndex).get(nextCityIndex) == 1) {
                count++;
            } else if (railwayMatrix.get(currentCityIndex).get(nextCityIndex) == 1) {
                count++;
            }
        }
        return count;
    }

    public int getTransportationTypeQ1(int cityIndex1, int cityIndex2, int highwayCount, int airwayCount, int railwayCount) {
        if (highwayMatrix.get(cityIndex1).get(cityIndex2) == 1 && highwayCount != 0) {
            return 1; // Highway
        } else if (airwayMatrix.get(cityIndex1).get(cityIndex2) == 1 && airwayCount != 0) {
            return 2; // Airway
        } else if (railwayMatrix.get(cityIndex1).get(cityIndex2) == 1 && railwayCount != 0) {
            return 3; // Railway
        } else {
            return 0; // No connection
        }
    }

    public void query2(String startCity, String destination, int stopCount) throws IOException {
        int startCityIndex = cityIndexMap.get(startCity);
        int destinationIndex = cityIndexMap.get(destination);

        List<Integer> currentPath = new ArrayList<>();
        boolean[] visitedCities = new boolean[cities.size()];

        query2(startCityIndex, destinationIndex, stopCount, currentPath, visitedCities);

        writeOutputQuery2(paths, startCity, destination, stopCount);
    }

    private void query2(int currentCityIndex, int destinationIndex, int stopCount, List<Integer> currentPath, boolean[] visitedCities) {
        visitedCities[currentCityIndex] = true;
        currentPath.add(currentCityIndex);

        if (currentCityIndex == destinationIndex && currentPath.size() - 2 == stopCount) {
            paths.add(new ArrayList<>(currentPath));
            visitedCities[currentCityIndex] = false;
            currentPath.remove(currentPath.size() - 1);
            return;
        }

        for (int neighborIndex = 0; neighborIndex < cities.size(); neighborIndex++) {
            if (!visitedCities[neighborIndex]) {
                query2(neighborIndex, destinationIndex, stopCount, currentPath, visitedCities);
            }
        }

        visitedCities[currentCityIndex] = false;
        currentPath.remove(currentPath.size() - 1);
    }

    private void writeOutputQuery2(List<List<Integer>> paths, String startCity, String destination, int stopCount) throws IOException {
        File file = new File("out2.txt");
        FileWriter fileWriter = new FileWriter(file);
        BufferedWriter buffW = new BufferedWriter(fileWriter);

        buffW.write("[Query 2] Paths from " + startCity + " to " + destination + " with " + stopCount + " intermediate cities:\n");

        if (paths.isEmpty()) {
            buffW.write("No valid paths found.");
        } else {
            for (List<Integer> path : paths) {
                for (int cityIndex : path) {
                    buffW.write(cities.get(cityIndex) + " ");
                }
                buffW.write("\n");
            }
        }
        buffW.close();
    }

    public void query3(String startCity, String destination, int transportationType) throws IOException {
        int startCityIndex = cityIndexMap.get(startCity);
        int destinationIndex = cityIndexMap.get(destination);

        List<Integer> currentPath = new ArrayList<>();
        boolean[] visitedCities = new boolean[cities.size()];

        query3(startCityIndex, destinationIndex, transportationType, currentPath, visitedCities, startCity, destination);
    }

    private void query3(int currentCityIndex, int destinationIndex, int transportationType, List<Integer> currentPath, boolean[] visitedCities, String startCity, String destination) throws IOException {
        visitedCities[currentCityIndex] = true;
        currentPath.add(currentCityIndex);

        if (currentCityIndex == destinationIndex) {
            writeOutputQuery3(currentPath, startCity, destination, transportationType);
            visitedCities[currentCityIndex] = false;
            currentPath.remove(currentPath.size() - 1);
            return;
        }

        for (int neighborIndex = 0; neighborIndex < cities.size(); neighborIndex++) {
            if (!visitedCities[neighborIndex] &&
                    getTransportationType(currentCityIndex, neighborIndex) == transportationType) {

                query3(neighborIndex, destinationIndex, transportationType, currentPath, visitedCities, startCity, destination);
            }
        }

        visitedCities[currentCityIndex] = false;
        currentPath.remove(currentPath.size() - 1);
    }

    private void writeOutputQuery3(List<Integer> path, String startCity, String destination, int transportationType) throws IOException {
        File file = new File("out3.txt");
        FileWriter fileWriter = new FileWriter(file);
        BufferedWriter buffW = new BufferedWriter(fileWriter);

        buffW.write("[Query 3] Paths from " + startCity + " to " + destination + " using transportation type " + getTransportationTypeName(transportationType) + ":\n");

        buffW.write(cities.get(path.get(0)));

        for (int i = 1; i < path.size(); i++) {
            buffW.write(" " + cities.get(path.get(i)));
        }
        buffW.write("\n");
        buffW.close();
    }

    public String getTransportationTypeName(int transportationType) {
        return switch (transportationType) {
            case 1 -> "Highway";
            case 2 -> "Airway";
            case 3 -> "Railway";
            default -> "Unknown";
        };
    }

    public int getTransportationType(int cityIndex1, int cityIndex2) {
        if (highwayMatrix.get(cityIndex1).get(cityIndex2) == 1) {
            return 1; // Highway
        } else if (airwayMatrix.get(cityIndex1).get(cityIndex2) == 1) {
            return 2; // Airway
        } else if (railwayMatrix.get(cityIndex1).get(cityIndex2) == 1) {
            return 3; // Railway
        } else {
            return 0;
        }
    }
}
