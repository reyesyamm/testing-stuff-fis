package entidad;

import android.widget.ArrayAdapter;

import com.swyam.fisiomer.R;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.swyam.fisiomer.Helpers.Capitalize;

/**
 * Created by Reyes Yam on 25/02/2017.
 */
public class Tratamiento implements Serializable{

    public static final Map<String,Integer> estados ;
    static{
        estados = new HashMap<>();
        estados.put("bien", R.drawable.cara1);
        estados.put("mediobien",R.drawable.cara2);
        estados.put("mal",R.drawable.cara3);
        estados.put("muymal",R.drawable.cara4);
    }

    public static final Map<Integer,Integer> estadosInt ;
    static{
        estadosInt = new HashMap<>();
        estadosInt.put(1, R.drawable.cara1);
        estadosInt.put(2,R.drawable.cara2);
        estadosInt.put(3,R.drawable.cara3);
        estadosInt.put(4,R.drawable.cara4);
    }

    public static final String lMusculos[] = {
            "Abductor del meñique (mano) (Abductor digiti minimi manus)",
            "Abductor del meñique (pie) (Abductor digiti minimi pedis)",
            "Aductor corto del muslo (Adductor brevis)",
            "Aductor del dedo gordo del pie (Adductor hallucis)",
            "Aductor del pulgar (Adductor pollicis)",
            "Aductor largo del muslo (Adductor longus)",
            "Aductor mayor del muslo (Adductor magnus)",
            "Anconeo (Anconeus)",
            "Antitrago, del (Antitragicus)",
            "Aritenoideo oblicuo (Arytaenoideus obliquus)",
            "Aritenoideo tranverso (Arytaenoideus transverso)",
            "Auricular anterior (Auricularis anterior)",
            "Auricular posterior (Auricularis posterior)",
            "Auricular superior (Auricularis superior)",
            "Biceps (Biceps brachii)",
            "Biceps femoral (Biceps femoris)",
            "Braquial anterior (Brachialis)",
            "Braquiorradial (Brachioradialis)",
            "Buccinador (Buccinator)",
            "Bulbocavernoso (Bulbospongiosus)",
            "Cigomatico mayor (Zygomaticus major)",
            "Cigomatico menor (Zygomaticus minor)",
            "Complejo mayor (Semispinalis cervicis)",
            "Constrictor inferior de la faringe (Constrictor pharyngis inferior)",
            "Constrictor medio de la faringe (Constrictor pharyngis medius)",
            "Constrictor superior de la faringe (Constrictor pharyngis superior)",
            "Coracobraquial (Coracobrachialis)",
            "Cremaster (Cremaster)",
            "Cricoarotineoideo lateral (Cricoarytaenoideus lateralis)",
            "Cricoarotinoideo posterior (Cricoarytaenoideus posterior)",
            "Cricotiroideo (Cricothyroideus)",
            "Crural (musculus biceps femoris)",
            "Cuadrado lumbar (Quadratus lumborum)",
            "Cuadrado plantar (Quadratus plantae)",
            "Cuadriceps (Quadriceps femoris: rectus femoris)",
            "Cuadriceps (Quadriceps femoris: vastus medialis)",
            "Cuadriceps (Quadriceps femoris: vastus lateralis)",
            "Cuadriceps (Quadriceps femoris: vastus intermedius)",
            "Cutaneo del cuello (Platysma mioides)",
            "Depresor del angulo de la boca",
            "Depresor del entrecejo",
            "Depresor del labio inferior",
            "Diafragma",
            "Elevador del angulo de la boca",
            "Elevador del ano",
            "Elevador del labio superior",
            "Esfinter interno del ano",
            "Estilohioideo",
            "Extensor corto de los dedos (pie)",
            "Extensor de los dedos",
            "Extensor largo del pulgar",
            "Flexor corto de los dedos (pie)",
            "Flexor corto del meñique",
            "Flexor corto del meñique (pie)",
            "Flexor corto del pulgar",
            "Flexor cubital del carpo",
            "Flexor largo de los dedos (pie)",
            "Flexor profundo de los dedos",
            "Flexor radial del carpo",
            "Flexor superficial de los dedos",
            "Frontal (flexores de los dedos)",
            "Gastrocnemio (Gastronecmius)",
            "Gemelo inferior (Gemellus inferior)",
            "Gemelo superior (Gemellus superior)",
            "Genihioideo (Geniohyoideus)",
            "Geniogloso (Genioglossus)",
            "Gluteo mayor (Gluteus maximus)",
            "Gluteo medio (Gluteus medius)",
            "Gluteo menor (Gluteus minimus)",
            "Gracil (Gracilis)",
            "Hiogloso (Hyoglossus)",
            "Iliaco (Iliacus)",
            "Ileocostales cervicales (Iliocostalis cervicis)",
            "Infraespinoso (Infraspinatus)",
            "Interoseos dorsales de la mano (Interossei dorsales manus)",
            "Interoseos dorsales del pie (Interossei dorsalis pedis)",
            "Interoseos palmares (Interossei palmares)",
            "Intercostales externos (Intercostalis externi)",
            "Intercostales internos (Intercostalis interni)",
            "Intercostales intimo (Intercostalis intimus)",
            "Intertransversos (Intertransversarii)",
            "Isquiocavernoso (Ischiocarvernosus)",
            "Isquiococcigeo, o isquiocoxígeo (Coccygeus",
            "Largo de la cabeza (Longissimus capitis)",
            "Largo del cuello (Longissimus cervicis)",
            "Longitudinal inferior de la lengua (Longitudinalis inferior linguae)",
            "Longitudinal superior de la lengua (Longitudinalis superior linguae)",
            "Lumbricales de la mano (Lumbricales manus)",
            "Lumbricales del pie (Lumbricales pedis)",
            "Musculo deltoides",
            "Musculo digastrico",
            "Musculo dorsal ancho",
            "Musculo elevador de la escapula",
            "Musculo elevador del parpado",
            "Musculo escaleno anterior",
            "Musculo escaleno medio",
            "Musculo escaleno posterior",
            "Musculo esfinter externo del ano",
            "Musculo esplenio",
            "Musculo esplenio de la cabeza",
            "Musculo esternocleidohioideo",
            "Musculo esternocleidomastoideo",
            "Musculo estilogloso",
            "Musculo extensor corto del pulgar",
            "Musculo extensor del indice",
            "Musculo extensor largo de los dedos (pie)",
            "Musculo extensor largo del dedo gordo",
            "Musculo extensor radial largo del carpo",
            "Musculo extensor ulnar del carpo",
            "Musculo flexor corto del dedo gordo (pie)",
            "Musculo peroneo anterior (Peroneus tertius)",
            "Musculo recto lateral de la cabeza (Rectus capitis lateralis)",
            "Musculo recto superior (Rectus superior)",
            "Masetero (Masseter)",
            "Mayor del helix (helicis major)",
            "Menor del helix (helicis minor)(en)",
            "Mentoniano (Mentalis)",
            "Milohioideo (Mylohyoideus)",
            "Nasal (Nasalis)",
            "Oblicuo inferior del ojo (Obliquus inferior bulbi)",
            "Oblicuo mayor del abdomen (Obliquus externus abdominis)",
            "Oblicuo menor del abdomen (Obliquus internus abdominis)",
            "Oblicuo superior del ojo (Obliquus superior bulbi)",
            "Obturador externo (Obturator externus)",
            "Obturador interno (Obturator internus)",
            "Occipital (Occipitalis)",
            "Occipitofrontal (occipitofrontalis)(en)",
            "Omohioideo (Omohyoideus)",
            "Oponente del meñique (Opponens digiti minimi)",
            "Oponente del pulgar (Opponens pollicis)",
            "Orbicular de la boca (Orbicularis oris)",
            "Orbicular de los ojos (Orbicularis oculi)",
            "Orbicular de los parpados (Orbicularis palpebral)",
            "Palmar corto (Palmaris brevis)",
            "Palmar largo (Palmaris longus)",
            "Pectineo (Pectineus)",
            "Pectoral mayor (Pectoralis major)",
            "Pectoral menor (Pectoralis minor)",
            "Peroneo lateral corto (Peroneus brevis)",
            "Peroneo lateral largo (Peroneus longus)",
            "Piloerector (erector del pelo)",
            "Piriforme de la pelvis (Piriformis)",
            "Plantar (Plantaris)",
            "Platisma (Platysma)",
            "Popliteo (Popliteus)",
            "Pronador cuadrado (Pronator quadratus)",
            "Pronador redondo (Pronator teres)",
            "Psoas mayor (Psoas major)",
            "Psoas menor (Psoas minor)",
            "Pterigoideo externo (Pterygoideus lateralis)",
            "Pterigoideo interno (Pterygoideus medialis)",
            "Recto anterior de la cabeza (Rectus capitis anterior)",
            "Recto del abdomen (Rectus abdominis)",
            "Recto inferior del ojo (Rectus inferior bulbi)",
            "Recto lateral del ojo (Rectus lateralis bulbi)",
            "Recto medial del ojo (Rectus medialis bulbi)",
            "Redondo mayor (Teres major)","Redondo menor (Teres minor)",
            "Risorio (risorius)","Romboides mayor (Rhomboideus major)",
            "Romboides menor (Rhomboideus menor)",
            "Soleo (Soleus)",
            "Sartorio o pladipus (Sartorius pladypusiensus)",
            "Semimembranoso (Semimembranosus)",
            "Semitendinoso (Semitendinosus)",
            "Serrato anterior (Serratus anterior)",
            "Serrato posterior inferior (Serratus posterior inferior)",
            "Serrato posterior superior (Serratus posterior superior)",
            "Subclavio (subclavius)",
            "Subescapular (Subscapularis)",
            "Superciliar (Corrugator supercilii)",
            "Supinador (Supinator)",
            "Supraespinoso (Supraspinatus)",
            "Suspensorio del duodeno (Suspensorius duodeni)",
            "Temporal (Temporalis)",
            "Temporoparietales (Temporoparietalis)",
            "Tensor de la fascia lata (Tensor fasciae latae)",
            "Tibial anterior (Tibialis anterior)",
            "Tibial posterior (Tibialis posterior)",
            "Tiroaritenoideo (Thyreoarytaenoideus)",
            "Tirohioideo (Thyreohyoideus)",
            "Triceps braquial (Triceps brachii)",
            "Trago (Tragicus)",
            "Transverso lingual (Transversus linguae)",
            "Transverso nasal (Compressor naris)",
            "Transverso profundo del perine (Transversus perinei profundus)",
            "Transverso superficial del perine (Transversus perinei superficialis)",
            "Tranverso del abdomen (Transversus abdominis)",
            "Trapecio (Trapezius)",
            "Vasto externo (Vastus lateralis)",
            "Vasto intermedio (Vastus intermedius)",
            "Vasto interno (Vastus medialis)"
    };

    public static final String lArticulacion[] = {
            "Cervical","Escapular","Hombro","Codo","Muñeca","Columna Torácica","Columna Lumbral","Cadera",
            "Rodilla","Tobillo"
    };

    public int id;
    public int estadoInicio;
    public int estadoFin;
    public Date fecha;
    public String terapeuta;
    public boolean tieneTFuncionales;
    public boolean tieneTPreventivos;
    public boolean tieneTAnalgesicos;

    public List<TratamientoFuncional> tfun = new ArrayList<>();
    public List<TratamientoPreventivo> tprev = new ArrayList<>();
    public List<TratamientoAnalgesico> tanal = new ArrayList<>();
    public List<Multimedia> lmul = new ArrayList<>();

    public Tratamiento(){

    }

    public Tratamiento(
            int id,
            String estadoInicio,
            String estadoFin,
            Date fecha,
            String terapeuta,
            boolean tieneTF,
            boolean tieneTP,
            boolean tieneTA
    ){
        this.id = id;
        this.estadoInicio = estados.get(estadoInicio);
        this.estadoFin = estados.get(estadoFin);
        this.fecha = fecha;
        this.terapeuta = terapeuta;
        tieneTFuncionales = tieneTF;
        tieneTPreventivos = tieneTP;
        tieneTAnalgesicos = tieneTA;
    }

    public String obtenerResumen(){
        String resumen="Se aplicaron los siguientes tratamientos \n";
        if(this.tfun!=null && this.tfun.size()>0){
            resumen+=(this.tfun.size()+" tratamientos funcionales: "+Capitalize(this.tfun.get(0).obtenerStrTratamiento())+"...\n");
        }

        if(this.tprev!=null && this.tprev.size()>0){
            resumen+=(this.tprev.size()+" tratamientos preventivos: "+Capitalize(this.tprev.get(0).obtenerTratamientoStr())+"...\n");
        }

        if(this.tanal!=null && this.tanal.size()>0){
            resumen+=(this.tanal.size()+" tratamientos analgésicos: "+Capitalize(this.tanal.get(0).obtenerTratamientoStr())+"...\n");
        }


        return resumen;
    }

    public void asignarTratamientosFuncionales(List<TratamientoFuncional> tfun){
        this.tfun = tfun;
    }

    public void asignarTratamientoPreventivo(List<TratamientoPreventivo> tprev){
        this.tprev = tprev;
    }

    public void asignarTratamientoAnalgesico(List<TratamientoAnalgesico> tanal){
        this.tanal = tanal;
    }

    public void asignarListaMultimedia(List<Multimedia> lmul){
        this.lmul = lmul;
    }

    public void agregarTratamientoFuncional(TratamientoFuncional tf){
        this.tfun.add(tf);
    }

    public void agregarTratamientoPreventivo(TratamientoPreventivo tp){
        this.tprev.add(tp);
    }

    public void agregarTratamientoAnalgesico(TratamientoAnalgesico ta){
        this.tanal.add(ta);
    }

    public int totalTratamientos(){
        return tfun.size()+tprev.size()+tanal.size();
    }

    public int totalFun(){
        return tfun.size();
    }

    public int totalPrev(){
        return tprev.size();
    }

    public int totalAna(){
        return tanal.size();
    }

    public int totalMultimedia(){return lmul.size(); }

    public String getFechaFormateada(){
        //SimpleDateFormat format = new SimpleDateFormat("MMMM E, yyyy hh:mm a");
        SimpleDateFormat format = new SimpleDateFormat("EEEE, d MMMM yy HH:mm aaa");
        return format.format(this.fecha);
    }

    public static String strMusculo(int index){
        if(index>=0 && index<lMusculos.length)
            return lMusculos[index];
        else
            return "Músculo desconocido";
    }

    public static String strArticulacion(int index){
        if(index>=0 && index<lArticulacion.length)
            return lArticulacion[index];
        else
            return "Articulación desconocida";
    }

    public static int obtenerIndiceMusculo(String musculo){
        for(int i=0;i<lMusculos.length;i++)
            if(musculo.equals(lMusculos[i]))
                return i;

        return -1;
    }

}
