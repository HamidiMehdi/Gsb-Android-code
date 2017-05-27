package visiteur.gsb.fr.Technique;

import android.content.Context;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;

/**
 * Created by Mehdi on 20/02/17.
 */
public class MySingleton {

    private static MySingleton monInstance;
    private RequestQueue laRequestQueue ;
    private static Context monContext;

    private MySingleton(Context context){
        monContext = context;
        laRequestQueue = getRequestQueue();
    }


    public static synchronized MySingleton getInstance(Context context){
        if(monInstance == null){
            monInstance = new MySingleton(context);
        }
        return monInstance;
    }

    public RequestQueue getRequestQueue(){
        if(laRequestQueue == null){
            Cache cache = new DiskBasedCache(monContext.getCacheDir(), 10*1024*1024);
            Network network = new BasicNetwork(new HurlStack());
            laRequestQueue = new RequestQueue(cache, network);
            laRequestQueue.start();
        }
        return laRequestQueue;
    }
}
