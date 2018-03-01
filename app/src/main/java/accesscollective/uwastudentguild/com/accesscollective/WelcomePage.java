package accesscollective.uwastudentguild.com.accesscollective;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class WelcomePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle(getString(R.string.title_activity_welcome_page));

        TextView welcomePageTV = (TextView) findViewById(R.id.welcomePageTitle);
        welcomePageTV.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
    }

    public void goToSelectCampusActivity(View view) {
        Intent Intent = new Intent(this, SelectCampus.class);
        startActivity(Intent);
    }

    public void goToMainActivity(View view) {
        Intent Intent = new Intent(this, MainActivity.class);
        Intent.putExtra("CAMPUS_NAME", "UWA Crawley");
        startActivity(Intent);

    }

    public void goToMapActivity(View view) {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }
}
