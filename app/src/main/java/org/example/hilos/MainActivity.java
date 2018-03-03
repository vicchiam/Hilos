package org.example.hilos;

import android.app.Fragment;
import android.app.FragmentTransaction;
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

import org.example.hilos.asynctask.DownloadImages;
import org.example.hilos.asynctask.Primos;
import org.example.hilos.asynctask.PrimosInterface;
import org.example.hilos.asynctask.PrimosIntervalo;
import org.example.hilos.asynctask.PrimosIntervaloConcurrente;
import org.example.hilos.asynctask.PrimosOculto;
import org.example.hilos.servicios.CalculadoraSHA1;
import org.example.hilos.servicios.CalculadoraSHA1Broadcast;
import org.example.hilos.servicios.ServicioCronometro;
import org.example.hilos.servicios.ServicioMessenger;
import org.example.hilos.servicios.ServicioPrimo;
import org.example.hilos.servicios.ServicioPrimoBroadcast;

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
        else if (id == R.id.item_local_enlazado) {
            this.chooseFragment(6);
        }
        else if (id == R.id.item_mensajero) {
            this.chooseFragment(7);
        }
        else if (id == R.id.item_calculadora_sha1) {
            this.chooseFragment(8);
        }
        else if (id == R.id.item_primo_service) {
            this.chooseFragment(9);
        }
        else if (id == R.id.item_calculadora_sha1_intent) {
            this.chooseFragment(10);
        }
        else if (id == R.id.item_primo_service_broadcast) {
            this.chooseFragment(11);
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void chooseFragment(int pos) {
        switch (pos) {
            case 0:
                this.setFragment(new Primos());
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
                break;
            case 6:
                this.setFragment(new ServicioCronometro());
                break;
            case 7:
                this.setFragment(new ServicioMessenger());
                break;
            case 8:
                this.setFragment(new CalculadoraSHA1());
                break;
            case 9:
                this.setFragment(new ServicioPrimo());
                break;
            case 10:
                this.setFragment(new CalculadoraSHA1Broadcast());
                break;
            case 11:
                this.setFragment(new ServicioPrimoBroadcast());
                break;
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

