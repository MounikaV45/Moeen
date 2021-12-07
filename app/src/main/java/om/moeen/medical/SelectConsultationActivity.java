package om.moeen.medical;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class SelectConsultationActivity extends AppCompatActivity {
    RecyclerView rvLogos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_consultation);

        rvLogos = findViewById(R.id.rvLogos);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);

        MyListData[] myListData = new MyListData[]{
                new MyListData("Education", R.drawable.educ_advice),
                new MyListData("Family", R.drawable.family_cons),
                new MyListData("Psychology", R.drawable.physc_cons),
                new MyListData("selfdevelopment", R.drawable.self_dev),
                new MyListData("legal", R.drawable.legal_advice),
                new MyListData("social", R.drawable.social_cons)
        };

        MyItemRecyclerViewAdapter adapter = new MyItemRecyclerViewAdapter(this, myListData);
        rvLogos.setHasFixedSize(true);
        rvLogos.setLayoutManager(mLayoutManager);
        rvLogos.setAdapter(adapter);
    }

    public void goLogin(View view) {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
}