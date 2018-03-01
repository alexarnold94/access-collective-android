package accesscollective.uwastudentguild.com.accesscollective;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
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

        final HashMap<String,String> UWA = new HashMap<>();
        UWA.put("UWA Crawley", "35 Stirling Highway, Crawley WA 6009");
        UWA.put("UWA Albany", "35 Stirling Terrace, Albany WA 6330");
        UWA.put("UWA Claremont (Won't Work)", "Princess Rd & Bay Road, Claremont WA 6010");

        final List<HashMap<String,String>> listItems = new ArrayList<>();
        final SimpleAdapter adapter= new SimpleAdapter(this, listItems, R.layout.list_item,
                new String[] {"First Line", "Second Line"},
                new int[] {R.id.text1, R.id.text2});

        for (Map.Entry<String,String> pair:UWA.entrySet()) {
            HashMap<String,String> resultsMap = new HashMap<>();
            resultsMap.put("First Line", pair.getKey().toString());
            resultsMap.put("Second Line", pair.getValue().toString());
            listItems.add(resultsMap);
        }

        resultsListView.setAdapter(adapter);

        resultsListView.setOnItemClickListener(new OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // create intent to send info to main activity
                Intent intent;
                intent = new Intent(SelectCampus.this, MainActivity.class);

                // get hashmap pair (campus name & address) based off list item selected
                HashMap<String, String> test = listItems.get(i);

                for (Map.Entry<String,String> pair : test.entrySet()) {
                    //send campus name to the main activity
                    intent.putExtra("CAMPUS_NAME", pair.getValue().toString());

                }
                startActivity(intent);
            }

        });
    }

}
