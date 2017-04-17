package com.swyam.fisiomer;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import entidad.Multimedia;
import entidad.Tratamiento;
import entidad.TratamientoAnalgesico;
import entidad.TratamientoFuncional;
import entidad.TratamientoPreventivo;

public class TreatmentDetailsActivity extends AppCompatActivity
                                        implements GenTreatmentFragment.OnFragmentInteractionListener, FunTreatmentFragment.OnFragmentInteractionListener,
                                        PrevTreatmentFragment.OnFragmentInteractionListener, AnaTreatmentFragment.OnFragmentInteractionListener, MultimediaFragment.OnFragmentInteractionListener{

    Context context;
    Tratamiento tratamiento;
    String nombrePaciente;
    Toolbar toolbar;
    ViewPager viewPager;
    TabLayout tabLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treatment_details);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                finish();
            }
        });

        nombrePaciente = getIntent().getStringExtra("paciente");
        toolbar.setSubtitle(nombrePaciente);

        tratamiento = (Tratamiento) getIntent().getSerializableExtra("tratamiento");
        viewPager = (ViewPager) findViewById(R.id.vp_tabs);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new GenTreatmentFragment(), "General");
        if(tratamiento.totalFun()>0)
            adapter.addFragment(new FunTreatmentFragment(), "Funcionales");

        if(tratamiento.totalPrev()>0)
            adapter.addFragment(new PrevTreatmentFragment(), "Preventivos");

        if(tratamiento.totalAna()>0)
            adapter.addFragment(new AnaTreatmentFragment(), "Analgésicos");


        if(tratamiento.totalMultimedia()>0)
            adapter.addFragment(new MultimediaFragment(),"Multimedia");


        viewPager.setAdapter(adapter);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public Tratamiento obtenerTratamiento(){
        return tratamiento;
    }

    public List<TratamientoFuncional> obtenerTratamientosFun(){
        return tratamiento.tfun;
    }

    public List<TratamientoPreventivo> obtenerTratamientosPrev(){
        return tratamiento.tprev;
    }

    public List<TratamientoAnalgesico> obtenerTratamientosAn(){
        return tratamiento.tanal;
    }

    public String nombrePaciente(){
        return toolbar.getSubtitle().toString();
    }

    public List<Multimedia> obtenerArchivosMultimedia(){ return tratamiento.lmul; }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


}
