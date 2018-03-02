package org.example.hilos;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Navigation Drawer
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.drawer_open, R.string.drawer_close);
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        drawer.addDrawerListener(toggle);
        toggle.syncState();//Pone las tres lineas

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {
            chooseFragment(0);
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.item_primos) {
            this.chooseFragment(0);
        } else if (id == R.id.item_primos_interface) {
            this.chooseFragment(1);
        }
        else if (id == R.id.item_primos_ocultos) {
            this.chooseFragment(2);
        }
        else if (id == R.id.item_primos_intervalo) {
            this.chooseFragment(3);
        }
        else if (id == R.id.item_primos_concurrente) {
            this.chooseFragment(4);
        }
        else if (id == R.id.item_imagenes) {
            this.chooseFragment(5);
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void chooseFragment(int pos) {
        switch (pos) {
            case 0:
                this.setFragment(new PrimosFragment());
                break;
            case 1:
                this.setFragment(new PrimosInterface());
                break;
            case 2:
                this.setFragment(new PrimosOculto());
                break;
            case 3:
                this.setFragment(new PrimosIntervalo());
                break;
            case 4:
                this.setFragment(new PrimosIntervaloConcurrente());
                break;
            case 5:
                this.setFragment(new DownloadImages());
            default:
                break;
        }
    }

    private void setFragment(Fragment fragment) {
        Log.e("Fragment", fragment.toString());
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.main_content, fragment);
        transaction.commit();
        Log.e("Fragment", "Anyadido");
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

}

