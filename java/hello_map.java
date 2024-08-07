import java.util.HashMap;
import java.util.Map;

public class MapExamples {
    public static void main(String[] args) {

        // std::map<std::string, int> m;
        Map<String, Integer> map = new HashMap<>();

        // adding key-value pairs
        map.put("Apple", 10); // <String, Integer> : <key, value>
        map.put("Banana", 20);
        map.put("Orange", 30);

        // retrieving a vvalue
        int appleCount = map.get("Apple");
        System.out.println("Apple count: " + appleCount); 
        // Apple count: 10


        // check if a key exists
        boolean hasBanana = map.containsKey("Banana");
        System.out.println("Contains Banana: " + hasBanana);
        // Contains Banana: true


        // iterating over keys
        for (String key : map.keySet()) {
            // for key in map.keySet():
            System.out.println(key + " -> " + map.get(key));
        }
        // Apple -> 10
        // Orange -> 30
        // Banana -> 20


        // removing a key-value pair
        map.remove("Orange");
        

        // size of the map
        System.out.println("Map size: " + map.size());
        // Map size: 2

    }
}
