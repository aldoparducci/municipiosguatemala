/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.KeyspaceMetadata;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.TableMetadata;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author personal
 */
@WebServlet(urlPatterns = {"/dep"})
public class dep extends HttpServlet {
    
    //Para conectarse a cassandra
    Cluster cluster = Cluster.builder().addContactPoint("127.0.0.1").build();
    //nos conectamos a la bd test
    Session session = cluster.connect("test");
    //metadata de la bd
    KeyspaceMetadata ks = cluster.getMetadata().getKeyspace("test");
    
    //para json
    Gson gson = new Gson();
    

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String funcion = URLDecoder.decode(request.getParameter("f"),"utf-8");
        switch (funcion){
            case "getDepartamentos":
                getDepartamentos(request,response);
                return;
            case "getMunicipios":
                getMunicipios(request,response);
                return;
            case "agregarPersona":
                agregarPersona(request,response);
                return;
            case "getPersonas":
                getPersonas(request,response);
                return;
                
        }
    }
    
    
    
    private void getDepartamentos(HttpServletRequest request, HttpServletResponse response) throws IOException{
        //verifica si la tabla departamentos existe
        TableMetadata table = ks.getTable("departamentos");
        if(table == null){
            crearDepartamentos();
        }
        
        String query = "SELECT nombre FROM departamentos;";
        ResultSet res = session.execute(query);
        List<Row> listado = res.all();
        ArrayList<String> arr = new ArrayList<>();
        //recorre todos los departamentos para meterlos en una lista
        for(Row r : listado){
//            //JsonElement val =  gson.toJsonTree(r.get(0, JsonElement.class));
           String val = r.getString(0);
           arr.add(val);
        }
        
        response.setContentType("application/json; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        //envia la lista en json
        response.getWriter().write(gson.toJson(arr));
    }
    private void getMunicipios(HttpServletRequest request, HttpServletResponse response) throws IOException{
        
        String departamento = URLDecoder.decode(request.getParameter("d"),"utf-8");
        
        String query = "SELECT JSON municipios FROM departamentos WHERE nombre='"+departamento+"'";
        ResultSet res = session.execute(query);
        
        
        response.setContentType("application/json; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write((res.one().getString(0)));
    }
    
    private void agregarPersona(HttpServletRequest request, HttpServletResponse response) throws IOException{
        String nombre = URLDecoder.decode(request.getParameter("nombre"),"utf-8");
        String departamento = URLDecoder.decode(request.getParameter("departamento"),"utf-8");
        String municipio = URLDecoder.decode(request.getParameter("municipio"),"utf-8");

        session.execute("INSERT INTO personas (uid, nombre, departamento, municipio) VALUES (uuid(), ?, ?, ?)",nombre,departamento,municipio);
        
        response.getWriter().write("Ok");
    }
    private void getPersonas(HttpServletRequest request, HttpServletResponse response) throws IOException{

        TableMetadata table = ks.getTable("personas");
        if(table == null){
            crearTablaPersonas();
        }
        
        String query = "SELECT JSON * FROM personas";
        ResultSet res = session.execute(query);
        List<Row> listado = res.all();
        ArrayList<JsonObject> arr = new ArrayList<>();
        //recorre todos las personas para meterlos en una lista
        for(Row r : listado){
           String val = r.getString(0);
           arr.add(gson.fromJson(val, JsonObject.class));
        }
        response.setContentType("application/json; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(gson.toJson(arr));
    }
    
    
    private void crearDepartamentos(){
        //Crearla tabla
        String query = "CREATE TABLE departamentos(nombre text PRIMARY KEY, "
        + "municipios list<text> ); ";
        
        session.execute(query);
        
        String data = "*Alta Verapaz.\n" +
"Cobán\n" +
"Santa Cruz Verapaz\n" +
"San Cristobal Verapaz\n" +
"Tactíc\n" +
"Tamahú\n" +
"San Miguel Tucurú\n" +
"Panzos\n" +
"Senahú\n" +
"San Pedro Carchá\n" +
"SanJuan Chamelco\n" +
"Lanquín\n" +
"Santa María Cahabón\n" +
"Chisec\n" +
"Chahal\n" +
"Fray Bartolomé de las Casas\n" +
"Santa Catarina La Tinta\n" +
"*Baja Verapaz.\n" +
"Salamá\n" +
"San Miguel Chicaj\n" +
"Rabinal\n" +
"Cubulco\n" +
"Granados\n" +
"Santa Cruz El Chol\n" +
"San Jerónimo\n" +
"Purulhá\n" +
"*Chimaltenango.\n" +
"Chimaltenango\n" +
"San José Poaquil\n" +
"San Martín Jilotepeque\n" +
"San Juan Comalapa\n" +
"Santa Apolonia\n" +
"Tecpán Guatemala\n" +
"Patzun\n" +
"San Miguel Pochuta\n" +
"Patzicia\n" +
"Santa Cruz Balanyá\n" +
"Acatenango\n" +
"San Pedro Yepocapa\n" +
"San Andrés Itzapa\n" +
"Parramos\n" +
"Zaragoza\n" +
"El Tejar\n" +
"*Chiquimula.\n" +
"Chiquimula\n" +
"San José La Arada\n" +
"San Juan Hermita\n" +
"Jocotán\n" +
"Camotán\n" +
"Olopa\n" +
"Esquipulas\n" +
"Concepción Las Minas\n" +
"Quezaltepeque\n" +
"San Jacinto\n" +
"Ipala\n" +
"*El Petén.\n" +
"Flores\n" +
"San José\n" +
"San Benito\n" +
"San Andrés\n" +
"La Libertad\n" +
"San Francisco\n" +
"Santa Ana\n" +
"Dolores\n" +
"San Luis\n" +
"Sayaxche\n" +
"Melchor de Mencos\n" +
"Poptún\n" +
"*El Progreso.\n" +
"Guastatoya\n" +
"Morazán\n" +
"San Agustín Acasaguastlan\n" +
"San Cristóbal Acasaguastlan\n" +
"El Jícaro\n" +
"Sansare\n" +
"Sanarate\n" +
"San Antonio La Paz\n" +
"*El Quiché.\n" +
"Santa Cruz del Quiche\n" +
"Chiche\n" +
"Chinique\n" +
"Zacualpa\n" +
"Chajul\n" +
"Santo Tomás Chichicstenango\n" +
"Patzité\n" +
"San Antonio Ilotenango\n" +
"San Pedro Jocopilas\n" +
"Cunén\n" +
"San Juan Cotzal\n" +
"Joyabaj\n" +
"Santa María Nebaj\n" +
"San Andrés Sajcabajá\n" +
"San Miguel Uspatán\n" +
"Sacapulas\n" +
"San Bartolomé Jocotenango\n" +
"Canilla\n" +
"Chicaman\n" +
"Playa Grnade - Ixcán\n" +
"Pachalúm\n" +
"*Escuintla.\n" +
"Escuintla\n" +
"Santa Lucía Cotzumalguapa\n" +
"La Democracia\n" +
"Siquinalá\n" +
"Masagua\n" +
"Pueblo Nuevo Tiquisate\n" +
"La Gomera\n" +
"Guanagazapa\n" +
"Puerto de San José\n" +
"Iztapa\n" +
"Palín\n" +
"San Vicente Pacaya\n" +
"Nueva Concepción\n" +
"*Guatemala.\n" +
"Guatemala\n" +
"Santa Catarina Pinula\n" +
"San José Pinula\n" +
"San José del Golfo\n" +
"Palencia\n" +
"Chinautla\n" +
"San Pedro Ayampuc\n" +
"Mixco\n" +
"San Pedro Sacatepequez\n" +
"San Juan Sacatepequez\n" +
"San Raymundo\n" +
"Chuarrancho\n" +
"Fraijanes\n" +
"Amatitlán\n" +
"Villa Nueva\n" +
"Villa Canales\n" +
"San Miguel Petapa\n" +
"*Huehuetenango.\n" +
"Huehuetenango\n" +
"Chiantla\n" +
"Malacatancito\n" +
"Cuilco\n" +
"Nentón\n" +
"San Pedro Necta\n" +
"Jacaltenango\n" +
"San Pedro Soloma\n" +
"San Ildelfonso Ixtahuac´n\n" +
"Santa Bárbara\n" +
"La Libertad\n" +
"La Democracia\n" +
"San Miguel Acatán\n" +
"San Rafael La Independencia\n" +
"Todos Santos Chuchcumatán\n" +
"San Juan Atitán\n" +
"Santa Eulalia\n" +
"San Mateo Ixtatán\n" +
"Colotenango\n" +
"San Sebastián Huehuetenango\n" +
"Tectitán\n" +
"Concepción Huista\n" +
"San Juan Ixcoy\n" +
"San Antonio Huista\n" +
"San Sebastián Coatán\n" +
"Santa Cruz Barillas\n" +
"Aguacatán\n" +
"San Rafael Petzal\n" +
"San Gaspar Ixchil\n" +
"Santiago Chimaltenango\n" +
"Santa Ana Huista\n" +
"*Izabal.\n" +
"Puerto Barrios\n" +
"Livingston\n" +
"El Estor\n" +
"Morales\n" +
"Los Amates\n" +
"*Jalapa.\n" +
"Jalapa\n" +
"San Pedro Pinula\n" +
"San Luis Jilotepeque\n" +
"San Manuel Chaparrón\n" +
"San Carlos Alzatate\n" +
"Monjas\n" +
"Mataquescuintla\n" +
"*Jutiapa.\n" +
"Jutiapa\n" +
"El Progreso\n" +
"Santa Catarina Mita\n" +
"Agua Blanca\n" +
"Asunción Mita\n" +
"Yupiltepeque\n" +
"Atescatempa\n" +
"Jerez\n" +
"El Adelanto\n" +
"Zapotitlán\n" +
"Comapa\n" +
"Jalpatagua\n" +
"Conguaco\n" +
"Moyuta\n" +
"Pasaco\n" +
"San José Acatempa\n" +
"Quezada\n" +
"*Quetzaltenango.\n" +
"Quetzaltenango\n" +
"Salcajá\n" +
"Olintepeque\n" +
"San Carlos Sija\n" +
"Sibilia\n" +
"Cabrican\n" +
"Cajola\n" +
"San Miguel Siguilça\n" +
"San Juan Ostuncalco\n" +
"San Mateo\n" +
"Concepción Chiquirichapa\n" +
"San Martín Sacatepequez\n" +
"Almolonga\n" +
"Cantel\n" +
"Huitán\n" +
"Zunil\n" +
"Colomba\n" +
"San Francisco La Unión\n" +
"El Palmar\n" +
"Coatepeque\n" +
"Génova\n" +
"Flores Costa Cuca\n" +
"La Esperanza\n" +
"Palestina de los Altos\n" +
"*Retalhuleu.\n" +
"Retalhuelu\n" +
"San Sebastián\n" +
"Santa Cruz Mulúa\n" +
"San Martín Zapotitlán\n" +
"San Felipe Retalhuleu\n" +
"San Andrés Villa Seca\n" +
"Champerico\n" +
"Nuevo San Carlos\n" +
"El Asintal\n" +
"*Sacatepéquez.\n" +
"Antigua Guatemala\n" +
"Jocotenango\n" +
"Pastores\n" +
"Sumpango\n" +
"Santo Domingo Xenacoj\n" +
"Santiago Sacatepequez\n" +
"San Bartolomé Milpas Altas\n" +
"San Lucas Sacatepequez\n" +
"Santa Lucía Milpas Altas\n" +
"Magdalena Milpas Altas\n" +
"Santa María de Jesús\n" +
"Ciudad Vieja\n" +
"San Miguel Dueñas\n" +
"San Juan Alotenango\n" +
"San Antonio Aguas Calientes\n" +
"Santa Catarina Barahona\n" +
"*San Marcos.\n" +
"San Marcos\n" +
"San Pedro Sacatepéquez\n" +
"Comitancillo\n" +
"San Antonio Sacatepéquez\n" +
"San Miguel Ixtahuacan\n" +
"Concepción Tutuapa\n" +
"Tacaná\n" +
"Sibinal\n" +
"Tajumulco\n" +
"Tejutla\n" +
"San Rafael Pié de la Cuesta\n" +
"Nuevo Progreso\n" +
"El Tumbador\n" +
"San José El Rodeo\n" +
"Malacatán\n" +
"Catarina\n" +
"Ayutla\n" +
"Ocos\n" +
"San Pablo\n" +
"El Quetzal\n" +
"La Reforma\n" +
"Pajapita\n" +
"Ixchiguan\n" +
"San José Ojetenán\n" +
"San Cristóbal Cucho\n" +
"Sipacapa\n" +
"Esquipulas Palo Gordo\n" +
"Río Blanco\n" +
"San Lorenzo\n" +
"*Santa Rosa.\n" +
"Cuilapa\n" +
"Berberena\n" +
"San Rosa de Lima\n" +
"Casillas\n" +
"San Rafael Las Flores\n" +
"Oratorio\n" +
"San Juan TEcuaco\n" +
"Chiquimulilla\n" +
"Taxisco\n" +
"Santa María Ixhuatan\n" +
"Guazacapán\n" +
"Santa Cruz Naranjo\n" +
"Pueblo Nuevo Viñas\n" +
"Nueva Santa Rosa\n" +
"*Sololá.\n" +
"Sololá\n" +
"San José Chacaya\n" +
"Santa María Visitación\n" +
"Santa Lucía Utatlán\n" +
"Nahualá\n" +
"Santa Catarina Ixtahuacán\n" +
"Santa Clara La Laguna\n" +
"Concepción\n" +
"San Andrés Semetabaj\n" +
"Panajachel\n" +
"Santa Catarina Palopó\n" +
"San Antonio Palopó\n" +
"San Lucas Tolimán\n" +
"Santa Cruz La Laguna\n" +
"Sna Pablo La Laguna\n" +
"San Marcos La Laguna\n" +
"San Juan La Laguna\n" +
"San Pedro La Laguna\n" +
"Santiago Atitlán\n" +
"*Suchitepéquez.\n" +
"Mazatenango\n" +
"Cuyotenango\n" +
"San Francisco Zapotitlán\n" +
"San Bernardino\n" +
"San José El Ídolo\n" +
"Santo Domingo Suchitepequez\n" +
"San Lorenzo\n" +
"Samayac\n" +
"San Pablo Jocopilas\n" +
"San Antonio Suchitepéquez\n" +
"San Miguel Panán\n" +
"San Gabriel\n" +
"Chicacao\n" +
"Patulul\n" +
"Santa Bárbara\n" +
"San Juan Bautista\n" +
"Santo Tomás La Unión\n" +
"Zunilito\n" +
"Pueblo Nuevo Suchitepéquez\n" +
"Río Bravo\n" +
"*Totonicapán.\n" +
"Totonicapán\n" +
"San Cristóbal Totonicapán\n" +
"San Francisco El Alto\n" +
"San Andrés Xecul\n" +
"Momostenango\n" +
"Santa María Chiquimula\n" +
"Santa Lucía La Reforma\n" +
"San Bartolo Aguas Calientes\n" +
"*Zacapa.\n" +
"Zacapa\n" +
"Estanzuela\n" +
"Río Hondo\n" +
"gualán\n" +
"Teculután\n" +
"Usumatlán\n" +
"Cabañas\n" +
"San Diego\n" +
"La Unión\n" +
"Huite";
        
        Map<String,ArrayList<String>> m = new HashMap();
        String a[] = data.split("\\.");
        
        //iniciamos en 1 porque 0 siempre va ser alta verapaz
        String dep = a[0].replace("*", "");
        for(int i = 1; i<a.length;i++){
            
            String aa[] = a[i].split("\n");
            //iniciamos en 1 porque el primero siempre viene vacio
            for(int ii = 1; ii<aa.length;ii++){
                String val = aa[ii];
                //si contiene * es departamento
                if(val.contains("*")){
                    dep = val.replace("*", "");
                }else{
                    // si no es municipio y se agrega
                    
                    //el array que tiene todos los municipios del departamento
                    ArrayList mun = m.get(dep);
                    
                    //si aun no hay array se agrega.
                    if(mun == null){
                        mun = new ArrayList();
                    }
                    
                    //agrego el municipio al listado
                    mun.add(val);
                    
                    //escribo en el mapa
                    m.put(dep, mun);
                }
                
            }
        }
        
        
        //se recorre todo el mapa de esta manera para extraer la llave (departamento) y el valor (municipios)
        for(Map.Entry<String, ArrayList<String>> o : m.entrySet()){
            //departamento
            String k = o.getKey();
            //municipios
            ArrayList lista = o.getValue();
            //agrega a la bd
            session.execute("INSERT INTO departamentos (nombre, municipios) VALUES (?, ?)", k,lista);
        }
        

        
    }
    private void crearTablaPersonas(){
        String query = "CREATE TABLE personas(uid uuid PRIMARY KEY, "
        + "nombre text,"
        + "departamento text,"
        + "municipio text ); ";
        
        session.execute(query);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
//        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
