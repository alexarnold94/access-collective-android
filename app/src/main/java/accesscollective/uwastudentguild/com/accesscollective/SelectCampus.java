package accesscollective.uwastudentguild.com.accesscollective;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;




public class SelectCampus extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_campus);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("Select Campus");


        ListView resultsListView= (ListView) findViewById(R.id.results_listview);

        HashMap<String,String> UWA = new HashMap<>();
        UWA.put("UWA Crawley", "35 Stirling Highway, Crawley WA 6009");
        UWA.put("UWA Claremont", "Princess Rd & Bay Road, Claremont WA 6010");
        UWA.put("UWA Albany", "35 Stirling Terrace, Albany WA 6330");

        List<HashMap<String,String>> listItems = new ArrayList<>();
        SimpleAdapter adapter= new SimpleAdapter(this, listItems, R.layout.list_item,
                new String[] {"First Line", "Second Line"},
                new int[] {R.id.text1, R.id.text2});

        Iterator it= UWA.entrySet().iterator();
        while(it.hasNext()) {
            HashMap<String,String> resultsMap = new HashMap<>();
            Map.Entry pair= (Map.Entry)it.next();
            resultsMap.put("First Line", pair.getKey().toString());
            resultsMap.put("Second Line", pair.getValue().toString());
            listItems.add(resultsMap);
        }

        resultsListView.setAdapter(adapter);
    }
}
