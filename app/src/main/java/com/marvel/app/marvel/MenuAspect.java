package com.marvel.app.marvel;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MenuAspect extends AppCompatActivity {

    Button btnMenuAspectList;
    Button btnMenuAspectGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_aspect);

        btnMenuAspectList = (Button)findViewById(R.id.btnMenuAspectList);
        btnMenuAspectGrid = (Button)findViewById(R.id.btnMenuAspectGrid);

        btnMenuAspectList.setOnClickListener(GoToList);
        btnMenuAspectGrid.setOnClickListener(GoToGrid);

    }

    private View.OnClickListener GoToList = new View.OnClickListener()
    {
        public void onClick(View v)
        {
            try
            {
                Intent intent = new Intent(v.getContext(), MainActivity.class);
                startActivity(intent);

            }
            catch(Exception e)
            {

            }
        }
    };

    private View.OnClickListener GoToGrid = new View.OnClickListener()
    {
        public void onClick(View v)
        {
            try
            {
                Intent intent = new Intent(v.getContext(), GridAspect.class);
                startActivity(intent);

            }
            catch(Exception e)
            {

            }
        }
    };
}
