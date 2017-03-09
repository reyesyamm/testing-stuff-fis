package com.swyam.fisiomer;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import entidad.Tratamiento;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GenTreatmentFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GenTreatmentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GenTreatmentFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";



    TextView fechaTratamiento, nombrePaciente, nombreTerapeuta,totalFun, totalPrev, totalAnal;
    ImageView imagenEstadoIni, imagenEstadoFin;
    View view;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public GenTreatmentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GenTreatmentFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GenTreatmentFragment newInstance(String param1, String param2) {
        GenTreatmentFragment fragment = new GenTreatmentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    Tratamiento tratamiento = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        tratamiento= ((TreatmentDetailsActivity)getActivity()).obtenerTratamiento();


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_gen_treatment, container, false);
        fechaTratamiento = (TextView)view.findViewById(R.id.tv_fecha_tratamiento);
        nombrePaciente = (TextView) view.findViewById(R.id.tv_nombre_paciente);
        nombreTerapeuta = (TextView) view.findViewById(R.id.tv_nombre_terapeuta);
        imagenEstadoIni = (ImageView) view.findViewById(R.id.image_estado_paciente_inicio);
        imagenEstadoFin = (ImageView) view.findViewById(R.id.image_estado_paciente_fin);
        totalFun = (TextView) view.findViewById(R.id.tv_total_tratamientos_funcionales);
        totalPrev = (TextView) view.findViewById(R.id.tv_total_tratamientos_preventivos);
        totalAnal = (TextView) view.findViewById(R.id.tv_total_tratamientos_analgesicos);
        if(tratamiento!=null){
            String nPaciente = ((TreatmentDetailsActivity)getActivity()).nombrePaciente();
            fechaTratamiento.setText(tratamiento.getFechaFormateada());
            nombrePaciente.setText("Paciente: "+nPaciente);
            nombreTerapeuta.setText("Terapeuta: "+tratamiento.terapeuta);
            imagenEstadoIni.setImageResource(tratamiento.estadoInicio);
            imagenEstadoFin.setImageResource(tratamiento.estadoFin);
            totalFun.setText("Se realizaron "+tratamiento.totalFun()+" tratamientos funcionales");
            totalPrev.setText("Se realizaron "+tratamiento.totalPrev()+" tratamientos preventivos");
            totalAnal.setText("Se realizaron "+tratamiento.totalAna()+" tratamientos analgésicos");
        }else{
            Log.d("fragment","tratamiento es null");
        }
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
   public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
