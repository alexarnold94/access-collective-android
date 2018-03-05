package accesscollective.uwastudentguild.com.accesscollective;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.util.Linkify;
import android.view.MenuItem;
import android.widget.TextView;

/**
 * Created by John on 19/02/2018.
 */

public class ContactsActivity  extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        setTitle("Emergency Contacts");

        /*
         Making numbers clickable
         */
        TextView textViewLifeThreatNum = (TextView) findViewById(R.id.textViewLifeThreateningNum);
        textViewLifeThreatNum.setText("123123123");
        Linkify.addLinks(textViewLifeThreatNum, Linkify.ALL);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
    }


}