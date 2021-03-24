import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

public class TestReadObject {

    public static void main(String[] args) throws Exception {
        try(ObjectInputStream i = new ObjectInputStream(new FileInputStream(new File("C:\\Users\\ojcch\\Documents" +
                "\\Projects\\Burt\\burt\\data\\graphs2\\19-com.evancharlton.mileage-3.0.8\\19-com.evancharlton.mileage-3.0.8-graph.obj")))){
            Object obj = i.readObject();
            System.out.println(obj);
        }
    }
}
