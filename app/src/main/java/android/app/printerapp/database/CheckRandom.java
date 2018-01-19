package android.app.printerapp.database;

/**
 * Created by Geek on 11/27/2017.
 */

public class CheckRandom {

    public int check_random(int ids[], int random){
        for (int i =0; i<ids.length;i++){
            if((ids[i] == random)){
                CheckRandom x = new CheckRandom();
                random = (int)(Math.random() *1000000000);
                x.check_random(ids, random);
            }
            else continue;
        }
        return random;
    }
}
