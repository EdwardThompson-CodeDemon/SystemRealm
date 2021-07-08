package com.realm.AnnotationProcessor;

import com.google.auto.service.AutoService;
import com.realm.annotations.DynamicClass;
import com.realm.annotations.DynamicProperty;
import com.realm.annotations.RealmDataClass;
import com.realm.annotations.SyncDescription;
import com.realm.annotations.db_class_;
import com.realm.annotations.sync_service_description;
import com.realm.annotations.sync_status;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
@SupportedAnnotationTypes({"com.realm.annotations.DynamicClass"})
public class Anna extends AbstractProcessor {
    Messager messager;
    Filer filer;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return super.getSupportedAnnotationTypes();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_8;
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {

        super.init(processingEnvironment);
        messager=processingEnvironment.getMessager();


        filer = processingEnvironment.getFiler();
        messager.printMessage(Diagnostic.Kind.NOTE, "Launched initialized");
        initd=true;
    }
    boolean initd=false;

    static Set<Element> getAnnotatedElements(
            Elements elements,
            TypeElement type,
            Class<? extends Annotation> annotation)
    {
        Set<Element> found = new HashSet<Element>();
        for (Element e : elements.getAllMembers(type)) {
            if (e.getAnnotation(annotation) != null)
                found.add(e);
        }
        return found;
    }
//   String package_name="sparta.realm.Dynamics";
//  String package_name="com.Thompsons.sparta.realm.v1.Dynamics";
   String package_name="RealmDynamics";
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
//if(initd){return true;}
//LinkedHashMap
        messager.printMessage(Diagnostic.Kind.NOTE, "Launched launcher");
        ArrayList<String> packages=new ArrayList<>();
        ArrayList<String> sync_packages=new ArrayList<>();
        ArrayList<sync_service_description> sync_descriptions=new ArrayList<>();
        HashMap<String,ArrayList<sync_service_description>> package_sync_descriptions=new HashMap<>();
        HashMap<String,String> package_table=new HashMap<>();
        HashMap<String,HashMap<String,Boolean>> table_columns=new HashMap<>();
        HashMap<String,String> table_create_sttment=new HashMap<>();
        HashMap<String,String> table_create_index_sttment=new HashMap<>();
        HashMap<String,String> delete_records_sttment=new HashMap<>();
        ArrayList<String> table_column_json=new ArrayList<>();
        ArrayList<String> tables=new ArrayList<>();
        HashMap<String,HashMap<String,String>> package_data_columns=new HashMap<>();
        HashMap<String,HashMap<String,String>> package_data_datatype=new HashMap<>();
        HashMap<String,HashMap<String,String>> package_json_column=new HashMap<>();
        HashMap<String,HashMap<String,String>> package_column_json=new HashMap<>();
       HashMap<String,HashMap<String,String>> package_column_data_type=new HashMap<>();
       HashMap<String,HashMap<String,String[]>> package_data_datatype_column_json=new HashMap<>();

        messager.printMessage(Diagnostic.Kind.NOTE, "Launched generated variables");

        db_class_ dbc=new db_class_();
        Field sid = null;
        try {
            sid = dbc.getClass().getField("sid");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        Field active_key = null;
        try {
            active_key = dbc.getClass().getField("data_status");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        // assert sid != null;
        String sid_json_key;
        String id_column_name="";
        try {
            sid_json_key = sid.getAnnotation(DynamicProperty.class).json_key();
        }finally {

        }
  try {
            id_column_name = dbc.getClass().getField("id").getAnnotation(DynamicProperty.class).column_name();
        }catch (NoSuchFieldException e) {
      e.printStackTrace();
  }finally {

        }

        String sid_column;
        try {
            sid_column = sid.getAnnotation(DynamicProperty.class).column_name();}finally {

        }
        String active_json_key="";
        try {
            active_json_key = active_key.getAnnotation(DynamicProperty.class).json_key();
        }finally {

        }

        messager.printMessage(Diagnostic.Kind.NOTE, "Launched generated variables"+sid_column+" "+" "+active_json_key);



 for (Element element : roundEnvironment.getElementsAnnotatedWith(DynamicClass.class)) {
            TypeElement typeElement = (TypeElement) element;

            String packag_nm=typeElement.getQualifiedName().toString();
//            if(packag_nm.split(".").length<packag_nm.split(".").length)
//            {
//package_name=packag_nm;
//            }
            if(packag_nm.split(".").length<(package_name.contains(".")?package_name.split(".").length:10))
            {
                package_name=packag_nm;
            }else if((packag_nm.split(".").length==(package_name.contains(".")?package_name.split(".").length:10))&&packag_nm.length()<package_name.length())
            {
                package_name=packag_nm;
            }
            messager.printMessage(Diagnostic.Kind.NOTE, "Realm package :"+(package_name.contains(".")?package_name.substring(0,package_name.lastIndexOf(".")):packag_nm));

            if (element.getKind() != ElementKind.CLASS) {
                messager.printMessage(Diagnostic.Kind.ERROR, element.getSimpleName()+" isnt a class. only classes can be annotated with DynamicClass");
                return false;
            }


            if (element.getModifiers().contains(Modifier.PRIVATE)) {
                messager.printMessage(Diagnostic.Kind.ERROR, element.getSimpleName()+" isnt public. only public classes can be annotated with DynamicClass");
                return false;
            }
            if (element.getModifiers().contains(Modifier.ABSTRACT)) {
                messager.printMessage(Diagnostic.Kind.ERROR, element.getSimpleName()+" is  Abstract. only non Abstract classes can be annotated with DynamicClass");
                return false;
            }

           //   messager.printMessage(Diagnostic.Kind.NOTE, packag_nm+" is  OK "+packages.size());
            packages.add(packag_nm);
            DynamicClass ann = element.getAnnotation(DynamicClass.class);
            SyncDescription[] sd = element.getAnnotationsByType(SyncDescription.class);
            package_table.put(packag_nm,ann.table_name());
            if( sd.length>0)
            {
                sync_packages.add(packag_nm);
                        ArrayList<sync_service_description> ssds=new ArrayList();
                for (SyncDescription s:sd)
                {
                    sync_service_description ssd=new sync_service_description();
                    ssd.service_name=s.service_name();
                    ssd.chunk_size=s.chunk_size();
                    ssd.download_link=s.download_link();
                    ssd.use_download_filter=s.use_download_filter();
                    ssd.servic_type=s.service_type();
                    ssd.table_filters=s.table_filters();
                    ssd.table_name=ann.table_name();
                    ssd.upload_link=s.upload_link();
                    ssd.object_package=packag_nm;
  ssd.download_array_position=s.download_array_position();
  ssd.upload_array_position=s.upload_array_position();
  ssd.is_ok_position=s.is_ok_position();


                    sync_descriptions.add(ssd);
                    ssds.add(ssd);

                }
                package_sync_descriptions.put(packag_nm,ssds);


            }


            HashMap<String,Boolean> column_index=new HashMap<>();
            HashMap<String,String> data_column=new HashMap<>();
            HashMap<String,String> data_datatype=new HashMap<>();
            HashMap<String,String> json_column=new HashMap<>();
            HashMap<String,String> column_json=new HashMap<>();
            HashMap<String,String> column_datatype=new HashMap<>();
            HashMap<String,String[]> data_datatype_column_json=new HashMap<>();

            Element sel=processingEnv.getTypeUtils().asElement(((TypeElement)element).getSuperclass());
            List<Element> all_elements=new ArrayList<>(element.getEnclosedElements());
            all_elements.addAll(sel.getEnclosedElements());
//            sel=processingEnv.getTypeUtils().asElement(((TypeElement)sel).getSuperclass());
//all_elements.addAll(sel.getEnclosedElements());

            while(!sel.getSimpleName().toString().equalsIgnoreCase("object"))
{
    sel=processingEnv.getTypeUtils().asElement(((TypeElement)sel).getSuperclass());
all_elements.addAll(sel.getEnclosedElements());
}
//            Element super_element=processingEnv.getTypeUtils().asElement(((TypeElement)element).getSuperclass());
//             messager.printMessage(Diagnostic.Kind.NOTE, sel.getSimpleName()+" Super el");
            String create_sttm = "CREATE TABLE \\\"\"+(copy?\"CP_\":\"\")+\""+ann.table_name()+"\\\"";
            String create_index_sttm = "";
            String qry="DELETE FROM "+ann.table_name()+" WHERE ("+sid_column+"='\"+sid+\"' OR "+sid_column+"=\"+sid+\") AND sync_status='"+ sync_status.syned.ordinal()+"'";

            delete_records_sttment.put(ann.table_name(),qry);
            boolean started=false;
            ArrayList<String> table_columns_i=new ArrayList<>();
            ArrayList<String> columns_all=new ArrayList<>();

            for (Element field : all_elements) {

                //   messager.printMessage(Diagnostic.Kind.NOTE, field.getSimpleName()+" FIELD");


                DynamicProperty dp=  field.getAnnotation(DynamicProperty.class);
                if(dp==null){
//                    messager.printMessage(Diagnostic.Kind.NOTE, field.getSimpleName()+" Isnt annotated Skipping ..");
                    continue;}
                String column_name=(dp.column_name().length()<1)?field.getSimpleName().toString():dp.column_name();
                try {
                    column_index.put(column_name,dp.indexed_column());
                    data_column.put(field.getSimpleName().toString(),column_name);
                    data_datatype.put(field.getSimpleName().toString(),field.asType().toString());
                   if(dp.json_key().length()>0&&!column_name.equalsIgnoreCase(id_column_name))
                    {
                        column_json.put(column_name,dp.json_key());
                        column_datatype.put(column_name,field.asType().toString());

                    }

                    if(dp.json_key().length()>0/*&&!column_name.equalsIgnoreCase(id_column_name)*/)
                    {
                        data_datatype_column_json.put(field.getSimpleName().toString(),new String[]{field.asType().toString(),column_name,dp.json_key()});

                    }

                        json_column.put(dp.json_key(),column_name);
                 //   messager.printMessage(Diagnostic.Kind.NOTE, "Field kind "+field.asType().toString()+" is  OK "+packages.size());
                    if(!started){
                        started=true;
                        create_sttm+=" (";
                    }
                    if(!columns_all.contains(column_name)){
                        create_sttm += "\\\""+column_name + "\\\" " + dp.column_data_type() + (dp.extra_params() == null || dp.extra_params().length() < 1 ? "" : " " + dp.extra_params()) + " " + (dp.column_default_value() == null || dp.column_default_value().length() < 1 ? "" : " DEFAULT " + dp.column_default_value()) + " ,";
                        if(dp.indexed_column())
                        {
                            create_index_sttm+="CREATE INDEX "+ann.table_name()+"_"+column_name+" ON "+ann.table_name()+"("+column_name+");";

                        }
                        columns_all.add(column_name);

                    }



                    table_columns_i.add("{\""+column_name+"\",\""+dp.json_key()+"\"}");

               //     messager.printMessage(Diagnostic.Kind.NOTE, column_name+" Column");



                }catch (Exception ex){
                    messager.printMessage(Diagnostic.Kind.ERROR, column_name+" Error "+ex.getMessage());

                }


            }
            messager.printMessage(Diagnostic.Kind.NOTE, "Adding tables ");
            table_column_json.add("{"+concat_string_p(table_columns_i)+"}");
            tables.add(ann.table_name());
            messager.printMessage(Diagnostic.Kind.NOTE, "Added tables ");
            if(started) {
                create_sttm = create_sttm.substring(0, create_sttm.length() - 1);
                create_sttm += " )";
            }
            if(create_index_sttm.endsWith(";")){create_index_sttm=create_index_sttm.substring(0,create_index_sttm.length()-1);}


            table_create_sttment.put(ann.table_name(),create_sttm);
            table_create_index_sttment.put(ann.table_name(),create_index_sttm);
            table_columns.put(ann.table_name(),column_index);
            package_data_columns.put(packag_nm,data_column);
            package_data_datatype.put(packag_nm,data_datatype);
            package_json_column.put(packag_nm,json_column);
            package_column_json.put(packag_nm,column_json);
            package_column_data_type.put(packag_nm,column_datatype);
            package_data_datatype_column_json.put(packag_nm,data_datatype_column_json);

            writeSourceFile(typeElement);
        }


        ClassName syncdescription = ClassName.get(sync_service_description.class);
        ClassName list = ClassName.get("java.util", "List");
        ClassName arrayList = ClassName.get("java.util", "ArrayList");
        ClassName jsonObject = ClassName.get("org.json","JSONObject");
        ClassName jsonArray = ClassName.get("org.json","JSONArray");
        ClassName cursor = ClassName.get("android.database","Cursor");
        TypeName result = ParameterizedTypeName.get(list, syncdescription);
        ClassName hashmap = ClassName.get(HashMap.class);
        TypeName hash_map_parammd = ParameterizedTypeName.get(hashmap, ClassName.get(String.class), ClassName.get(String.class));




        MethodSpec.Builder b13= MethodSpec.methodBuilder("getInsertStatementsFromJson")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(jsonArray, "array")
                .addParameter(String.class, "package_name")
                .addJavadoc("Returns most efficient direct insert queries as per sqlite 3.7 /nAdjust database compound Limit for optimization")
                 .addException(ClassName.get("org.json","JSONException"))
                .returns(String[][].class)
                .addCode("if(array.length()==0){return new String[][]{{\"()\",\"\"},{}};}")
                .addStatement("int compound_limit=500")
                .addStatement("int ar_sz=array.length()")
                .addStatement("int max=ar_sz<=compound_limit?1:ar_sz%compound_limit>0?(ar_sz/compound_limit)+1:(ar_sz/compound_limit)")
                .addStatement("String[] qryz=new String[max]")
                .addStatement("StringBuffer sb_sid=new StringBuffer(\"(\");")
                .addStatement("StringBuffer sb_sid_inactive=new StringBuffer(\"(\");")
                .beginControlFlow("switch(package_name)");//continue sedtting   up realm to get inactive ids

        for (String s:package_column_json.keySet()) {

            String columns=concat_map(package_column_json.get(s),true);
            String json_keys=concat_map(package_column_json.get(s),false);
            String columnz[]=split_string(columns,",");


            b13.addCode("case \""+s+"\" :\n");
            b13.beginControlFlow("for(int m=0;m<max;m++)");
            b13.addStatement("StringBuffer sb=new StringBuffer()");
             b13.addStatement("sb.append(\"REPLACE INTO "+package_table.get(s)+"("+columns+") \")");//(sid,member_id,acc_id,acc_name,sync_var) \")");
            b13.beginControlFlow("for(int s=0;s<(((m*compound_limit)+compound_limit)<=ar_sz?compound_limit:ar_sz-(m*compound_limit));s++)");
            b13.addStatement("int i=(m*compound_limit)+s");
            b13.addStatement("JSONObject jo= array.getJSONObject(i)");
            b13.addStatement("sb_sid.append(i==0?jo.getString(\"id\"):\",\"+jo.getString(\"id\"))");
//            b13.addStatement("sb_sid_inactive.append(jsonHasActiveKey(jo)?(i==0?jo.getString(\""+active_json_key+"\"):\",\"+jo.getString(\""+active_json_key+"\")):\"\")");
//            b13.addCode("sb.append(s==0?\"SELECT \"+(jo.getString(\"id\"))+\" AS sid,\"+\n" +
//                    "                               (jo.getString(\"member_id\"))+\" AS member_id,\"+\n" +
//                    "                                (jo.getString(\"acc_id\"))+\" AS acc_id,'\"+\n" +
//                    "                               (jo.getString(\"acc_name\"))+\"' AS acc_name,'\"+\n" +
//                    "                               (jo.getString(\"datecomparer\"))+\"' AS sync_var\":\n" +
//                    "                                \" UNION SELECT \"+(jo.getString(\"id\"))+\",\"+\n" +
//                    "                                        (jo.getString(\"member_id\"))+\",\"+\n" +
//                    "                                        (jo.getString(\"acc_id\"))+\",'\"+\n" +
//                    "                                        (jo.getString(\"acc_name\"))+\"','\"+\n" +
             b13.addCode("sb.append(s==0?\"SELECT \"+");
            int i=0;
            for (Map.Entry<String, String> st:package_column_json.get(s).entrySet()) {
//                if(st.getKey().equalsIgnoreCase("sync_status")) {
//                    b13.addCode((i == 0 ? "\"'\"+" : ",'\"+") + sync_status.syned.ordinal()+"+\"' AS sync_status");
//                }else {
//                    b13.addCode((i == 0 ? "\"'\"+" : ",'\"+") + "jo.optString(\"" + st.getValue() + "\")+\"' AS " + st.getKey());
//
//                }
                //   b13.addCode((i == 0 ? "\"'\"+" : ",'\"+") +(st.getKey().equalsIgnoreCase("sync_status")?sync_status.syned.ordinal(): "jo.optString(\"" + st.getValue() + "\")")+"+\"' AS " + st.getKey());


                String cov=package_column_data_type.get(s).get(st.getKey()).equalsIgnoreCase("int")||package_column_data_type.get(s).get(st.getKey()).equalsIgnoreCase("double")?"":"'";

                    b13.addCode((i == 0 ? "\""+cov+"\"+" : ","+cov+"\"+") +(st.getKey().equalsIgnoreCase("sync_status")?sync_status.syned.ordinal(): "jo.optString(\"" + st.getValue() + "\")")+"+\""+cov+" AS " + st.getKey());




               i++;
            }
            b13.addCode("\":\" UNION SELECT \"+");
            i=0;
            for (Map.Entry<String, String> st:package_column_json.get(s).entrySet()) {
                if(st.getKey().equalsIgnoreCase("sync_status")){
                //    b13.addCode((i==0?"\"'\"+":"+\",'\"+")+sync_status.syned.ordinal()+"+\"'\"");

                }else{
                  //  b13.addCode((i==0?"\"'\"+":"+\",'\"+")+"jo.optString(\""+st.getValue()+"\")+\"'\"");
                }
               // b13.addCode((i==0?"\"'\"+":"+\",'\"+")+(st.getKey().equalsIgnoreCase("sync_status")?sync_status.syned.ordinal():"jo.optString(\""+st.getValue()+"\")")+"+\"'\"");

                String cov=package_column_data_type.get(s).get(st.getKey()).equalsIgnoreCase("int")||package_column_data_type.get(s).get(st.getKey()).equalsIgnoreCase("double")?"":"'";
                b13.addCode((i==0?"\""+cov+"\"+":"+\","+cov+"\"+")+(st.getKey().equalsIgnoreCase("sync_status")?sync_status.syned.ordinal():"jo.optString(\""+st.getValue()+"\")")+"+\""+cov+"\"");

                i++;
            }
            b13.addStatement(")");
            b13.endControlFlow();
            b13.addStatement("qryz[m]=sb.toString()");
            b13.endControlFlow();
            b13.addStatement(" sb_sid.append(\")\");");
//            b13.addStatement("return new String[][]{new String[]{sb_sid.toString()},qryz}");
            b13.addStatement(" return new String[][]{new String[]{sb_sid.toString(),sb_sid_inactive.toString()},qryz}");
//            b13.addStatement("return qryz");



        }
        b13.endControlFlow();
        b13.addStatement("return null");






        MethodSpec.Builder b12 = MethodSpec.methodBuilder("getJsonFromCursor")
                .addModifiers(Modifier.PUBLIC)
                .addException(ClassName.get("org.json","JSONException"))
                .addParameter(Object.class, "o")
                .addParameter(String.class, "package_name")
                .returns(jsonObject)
                .addStatement(" Cursor c=(Cursor)o")
                .addStatement("List<String> colz=  Arrays.asList(c.getColumnNames())")
                .addStatement("JSONObject obj=new JSONObject()")
                .beginControlFlow("switch(package_name)");

        MethodSpec.Builder b11 = MethodSpec.methodBuilder("getObjectFromCursor")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(Object.class, "o")
                .addParameter(String.class, "package_name")
                .returns(Object.class)
                .addStatement("Cursor c=  (Cursor)o")
                .addStatement("List<String> colz=  Arrays.asList(c.getColumnNames())")
                .beginControlFlow("switch(package_name)");



        MethodSpec.Builder b10 = MethodSpec.methodBuilder("getTables")
                .addModifiers(Modifier.PUBLIC)
                .returns(String[].class)
                .addStatement("return new String[]{"+concat_string(tables)+"}");

        MethodSpec.Builder b9 = MethodSpec.methodBuilder("getTableColumnJson")
                .addModifiers(Modifier.PUBLIC)
                .returns(String[][][].class)
                .addStatement("return new String[][][]{"+concat_string_p(table_column_json)+"}");






        MethodSpec.Builder b8= MethodSpec.methodBuilder("getContentValuesFromJson")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(jsonObject, "json")
                .addParameter(String.class, "table_name")
                .addJavadoc("Returns true if the json does not have the active key defined in db_class or if the json has the key and the value is true")
                .addStatement("int table_index= $T.asList(getTables()).indexOf(table_name)",ClassName.get("java.util","Arrays"))
                .addStatement("String[][] column_json=getTableColumnJson()[table_index]")
                .addStatement("ContentValues cv=new ContentValues()")
                .beginControlFlow("for(int i =0;i<column_json.length;i++)")
                .beginControlFlow("try")
                .addStatement("cv.put(column_json[i][0],json.getString(column_json[i][1]))")
                .endControlFlow("catch($T e){}",ClassName.get("org.json","JSONException"))
                .endControlFlow("")
                .addStatement("return cv")
                .returns(ClassName.get("android.content","ContentValues"));

        MethodSpec.Builder b7= MethodSpec.methodBuilder("jsonHasActiveKey")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(jsonObject, "json")
                .addJavadoc("Returns true if the json does not have the active key defined in db_class or if the json has the key and the value is true")
                .addComment("custom comment")
                .beginControlFlow("try")
                .addStatement("if((json.has(\""+active_json_key+"\")&&json.getBoolean(\""+active_json_key+"\"))||!json.has(\""+active_json_key+"\")) return true")
                .endControlFlow("catch(Exception e){}")
                .addStatement("return false")

                .returns(Boolean.class);


        MethodSpec.Builder b6 = MethodSpec.methodBuilder("getDeleteRecordSttment")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(String.class, "table_name")
                .addParameter(String.class, "sid")
                .returns(String.class);

        MethodSpec.Builder b5 = MethodSpec.methodBuilder("getTableCreateIndexSttment")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(String.class, "table_name")
                .returns(String.class);

        MethodSpec.Builder b4 = MethodSpec.methodBuilder("getTableCreateSttment")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(String.class, "table_name")
                .addParameter(Boolean.class, "copy")
                .returns(String.class);

        MethodSpec.Builder b3 = MethodSpec.methodBuilder("getTableColumns")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(String.class, "table_name")
                .returns(hash_map_parammd)
                .addStatement("$T map = new $T<>()", hash_map_parammd, hashmap);


        MethodSpec.Builder b2 = MethodSpec.methodBuilder("getPackageTable")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(String.class, "package_name")
                .returns(String.class)
                .beginControlFlow("if (package_tables==null)")
                .addStatement("$T map = new $T<>()", hash_map_parammd, hashmap);


        MethodSpec.Builder b = MethodSpec.methodBuilder("getSyncDescription")
                .addModifiers(Modifier.PUBLIC)
                .returns(result)
                .addStatement("$T result = new $T<>()", result, arrayList);


        int i=0;
        for (sync_service_description s:sync_descriptions) {

            b.addStatement(" sync_service_description ssd_"+i+"=new sync_service_description();\n" +
                    "                   ssd_"+i+".service_name=\""+s.service_name+"\";\n" +
                    "                   ssd_"+i+".chunk_size="+s.chunk_size+";\n" +
                    "                   ssd_"+i+".download_link=\""+s.download_link+"\";\n" +
                    "                   ssd_"+i+".use_download_filter="+s.use_download_filter+";\n" +
                    "                   ssd_"+i+".servic_type=com.realm.annotations.SyncDescription.service_type.values()["+s.servic_type.ordinal()+"];\n" +
                    "                   ssd_"+i+".table_filters=new String[]{"+concat_string(s.table_filters)+"};\n" +
                    "                   ssd_"+i+".table_name=\""+s.table_name+"\";\n" +
                    "                   ssd_"+i+".upload_link=\""+s.upload_link+"\";\n" +
                    "                   ssd_"+i+".object_package=\""+s.object_package+"\";\n" +
                   "                   ssd_"+i+".download_array_position=\""+s.download_array_position+"\";\n" +
                   "                   ssd_"+i+".upload_array_position=\""+s.upload_array_position+"\";\n" +
                   "                   ssd_"+i+".is_ok_position=\""+s.is_ok_position+"\";\n" +
                   "                   ssd_"+i+".service_id=\""+s.service_id+"\";\n" +
                    "                   result.add(ssd_"+i+")");


            i++;
        }
        b.addStatement("return result");


  MethodSpec.Builder getSyncDescription = MethodSpec.methodBuilder("getSyncDescription")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(Object.class,"obj")
                .returns(result)
          .addStatement("$T result = new $T<>()", result, arrayList)

                 .addStatement("String ss=obj.getClass().getName()", result, arrayList);

        getSyncDescription.beginControlFlow("switch (ss)");

         i=0;


        for (Map.Entry<String, ArrayList<sync_service_description>> psd:package_sync_descriptions.entrySet()) {
            getSyncDescription.addCode( "case \""+psd.getKey()+"\":\n");

            for (sync_service_description s:psd.getValue()) {


                getSyncDescription.addStatement( "sync_service_description ssd_"+i+"=new sync_service_description();\n" +
                        "                   ssd_"+i+".service_name=\""+s.service_name+"\";\n" +
                        "                   ssd_"+i+".chunk_size="+s.chunk_size+";\n" +
                        "                   ssd_"+i+".download_link=\""+s.download_link+"\";\n" +
                        "                   ssd_"+i+".use_download_filter="+s.use_download_filter+";\n" +
                        "                   ssd_"+i+".servic_type=com.realm.annotations.SyncDescription.service_type.values()["+s.servic_type.ordinal()+"];\n" +
                        "                   ssd_"+i+".table_filters=new String[]{"+concat_string(s.table_filters)+"};\n" +
                        "                   ssd_"+i+".table_name=\""+s.table_name+"\";\n" +
                        "                   ssd_"+i+".upload_link=\""+s.upload_link+"\";\n" +
                        "                   ssd_"+i+".object_package=\""+s.object_package+"\";\n" +
                        "                   ssd_"+i+".download_array_position=\""+s.download_array_position+"\";\n" +
                        "                   ssd_"+i+".upload_array_position=\""+s.upload_array_position+"\";\n" +
                        "                   ssd_"+i+".is_ok_position=\""+s.is_ok_position+"\";\n" +
                        "                   result.add(ssd_"+i+")");




                i++;
            }

        }
        getSyncDescription.endControlFlow();
        getSyncDescription.addStatement("return result");

//change all ifs to switch cases
        for (Map.Entry<String, String> s:package_table.entrySet()) {

            b2.addStatement("map.put(\""+s.getKey()+"\",\""+s.getValue()+"\")");
        }
        b2.addStatement("package_tables=map");
        b2.endControlFlow();
        b2.addStatement("return (String)package_tables.get(package_name)");

        boolean begun_flow_1=false;
        for (Map.Entry<String, HashMap<String,Boolean>> s:table_columns.entrySet()) {

            if(!begun_flow_1){
                b3.beginControlFlow("if(table_name==\""+s.getKey()+"\")");
                begun_flow_1=true;
            }else {
                b3.nextControlFlow("else if(table_name==\""+s.getKey()+"\")");

            }
            int j=0;
            for (Map.Entry<String,Boolean> t:s.getValue().entrySet()) {

                b3.addStatement("map.put(\""+t.getKey()+"\",\""+t.getValue()+"\")");

                j++;
            }
            b3.addStatement("return map");


        }

        b3.nextControlFlow("else");
        b3.addStatement("return null");
        b3.endControlFlow();

        boolean begun_flow_2=false;
        for (Map.Entry<String, String> s:table_create_sttment.entrySet()) {

            if(!begun_flow_2){
                b4.beginControlFlow("if(table_name==\""+s.getKey()+"\")");
                begun_flow_2=true;
            }else {
                b4.nextControlFlow("else if(table_name==\""+s.getKey()+"\")");

            }


            b4.addStatement("return \""+s.getValue()+"\"");


        }
        b4.nextControlFlow("else");
        b4.addStatement("return null");
        b4.endControlFlow();


        boolean begun_flow_3=false;
        for (Map.Entry<String, String> s:table_create_index_sttment.entrySet()) {

            if(!begun_flow_3){
                b5.beginControlFlow("if(table_name==\""+s.getKey()+"\")");
                begun_flow_3=true;
            }else {
                b5.nextControlFlow("else if(table_name==\""+s.getKey()+"\")");

            }


            b5.addStatement("return \""+s.getValue()+"\"");


        }
        b5.nextControlFlow("else");
        b5.addStatement("return null");
        b5.endControlFlow();



        boolean begun_flow_4=false;
        for (Map.Entry<String, String> s:delete_records_sttment.entrySet()) {

            if(!begun_flow_4){
                b6.beginControlFlow("if(table_name==\""+s.getKey()+"\")");
                begun_flow_4=true;
            }else {
                b6.nextControlFlow("else if(table_name==\""+s.getKey()+"\")");

            }


            b6.addStatement("return \""+s.getValue()+"\"");


        }
        b6.nextControlFlow("else");
        b6.addStatement("return null");
        b6.endControlFlow();

/*

public static Object getObjectFromCursor(Cursor c, String pkg_name) {
      switch (pkg_name)
      {
          case "":
          member m=new member();
           List<String> colz=   Arrays.asList(c.getColumnNames());

                      try {
                          m.district.value = c.getString(colz.indexOf(""));
                      }finally {


                      }
                      return m;
      }
      return null;
  }
 */
        int k=0;
        for (String s:package_data_columns.keySet()) {



            b11.addCode("case \""+s+"\" :\n");
            b11.addStatement(s+" obj"+k+"=new "+s+"()");
            for (Map.Entry<String, String> st:package_data_columns.get(s).entrySet()) {
                b11.beginControlFlow("try");
                //  b11.addStatement("obj"+k+"."+st.getKey()+"=c."+(package_data_datatype.get(s).get(st.getKey()).equalsIgnoreCase("int")?"getInt":"getString")+"(colz.indexOf(package_name))");
                b11.addStatement("obj"+k+"."+st.getKey()+"=c."+(package_data_datatype.get(s).get(st.getKey()).equalsIgnoreCase("int")?"getInt":"getString")+"(colz.indexOf(\""+st.getValue()+"\"))");
                // b11.addComment("obj"+k+"."+st.getKey()+"=c."+(package_data_datatype.get(s).get(st.getKey())));
                b11.nextControlFlow("finally");
                b11.endControlFlow();
            }

            b11.addStatement("return obj"+k);

            k++;
        }
        b11.endControlFlow();

        b11.addStatement("return colz");




        for (String s:package_json_column.keySet()) {



            b12.addCode("case \""+s+"\" :\n");

//            for (Map.Entry<String, String> st:package_column_json.get(s).entrySet()) {

                for (Map.Entry<String, String[]> st:package_data_datatype_column_json.get(s).entrySet()) {
                b12.beginControlFlow("try");
//                b12.addStatement("obj.put(\""+st.getKey()+"\",c."+(package_data_datatype.get(s).get(st.getKey()).equalsIgnoreCase("int")?"getInt":"getString")+"(colz.indexOf(\""+st.getValue()+"\")))");
                b12.addStatement("obj.put(\""+st.getValue()[2]+"\",c."+(st.getValue()[0].equalsIgnoreCase("int")?"getInt":"getString")+"(colz.indexOf(\""+st.getValue()[1]+"\")))");


                b12.nextControlFlow("finally");
                b12.endControlFlow();
            }

            b12.addStatement("return obj");



        }
        b12.endControlFlow();

        b12.addStatement("return null");









        writePKGF(packages,sync_packages,getSyncDescription.build(),b.build(),b2.build(),b3.build(),b4.build(),
                b5.build(),b6.build(),b7.build(),b8.build(),b9.build(),b10.build(),
                b11.build(),b12.build(),b13.build());
        return true;
    }
    boolean writen_dyna_file=false;

    private void writePKGF(ArrayList<String> all_packages,ArrayList<String> sync_packages,
                           MethodSpec getSyncDescription,
                           MethodSpec getSyncDescriptions,
                           MethodSpec getPackageTable,
                           MethodSpec getTableColumns,
                           MethodSpec getTableCreateStatements,
                           MethodSpec getTableCreateIndexSttment,
                           MethodSpec getTableDeleteSttment,
                           MethodSpec jsonHasActiveKey,
                           MethodSpec getContentValuesFromJson,
                           MethodSpec getTableColumnJson,
                           MethodSpec getTables,
                           MethodSpec getObjectFromCursor,
                           MethodSpec getJsonFromCursor,
                           MethodSpec getInsertStatementsFromJson) {

        if(writen_dyna_file){
            return;
        }
        writen_dyna_file=true;




        MethodSpec getDynamicClassPaths = MethodSpec.methodBuilder("getDynamicClassPaths")
                .addModifiers(Modifier.PUBLIC)
                .returns(String[].class)
                .addStatement("return new String[]{"+concat_string(all_packages)+"}")
                .build();
        MethodSpec getDynamicSyncClassPaths = MethodSpec.methodBuilder("getDynamicSyncClassPaths")
                .addModifiers(Modifier.PUBLIC)
                .returns(String[].class)
                .addStatement("return new String[]{"+concat_string(sync_packages)+"}")
                .build();





        TypeSpec spartaDynamicsClass = TypeSpec.classBuilder("spartaDynamics")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .superclass(RealmDataClass.class)

                .addField(ClassName.get("android.database","Cursor"), "a_c", Modifier.PUBLIC, Modifier.STATIC)
//                .addField(HashMap.class, "table_columns", Modifier.PUBLIC, Modifier.STATIC)
//                .addField(String[].class, "tables", Modifier.PUBLIC, Modifier.STATIC)
//                .addField(String[][][].class, "table_column_json", Modifier.PUBLIC, Modifier.STATIC)//generate string and declare here
                .addMethod(getDynamicClassPaths)
                .addMethod(getDynamicSyncClassPaths)
                .addMethod(getSyncDescription)
                .addMethod(getSyncDescriptions)
                .addMethod(getPackageTable)
                .addMethod(getTableColumns)
                .addMethod(getTableCreateStatements)
                .addMethod(getTableCreateIndexSttment)
                .addMethod(getTableDeleteSttment)
                .addMethod(jsonHasActiveKey)
                .addMethod(getContentValuesFromJson)
                .addMethod(getTableColumnJson)
                .addMethod(getTables)
                .addMethod(getObjectFromCursor)
                .addMethod(getJsonFromCursor)
                .addMethod(getInsertStatementsFromJson)


                .build();

         JavaFile javaFile = JavaFile.builder(package_name.substring(0,package_name.lastIndexOf("."))+".RealmDynamics", spartaDynamicsClass)
        //   JavaFile javaFile = JavaFile.builder("sparta.realm.RealmDynamics", spartaDynamicsClass)
                .build();


//        messager.printMessage(Diagnostic.Kind.NOTE, javaFile.;);
        messager.printMessage(Diagnostic.Kind.NOTE, "Writing file file "+package_name.substring(0,package_name.lastIndexOf("."))+".RealmDynamics");
        try {
            javaFile.writeTo(filer);
        } catch (Exception e) {
            e.printStackTrace();
            Messager messager = processingEnv.getMessager();
            String message = String.format("Unable to write file: %s",
                    e.getMessage());
            messager.printMessage(Diagnostic.Kind.ERROR, message);
        }
    }

    public static <T> T[] concatenate(T[] a, T[] b) {
        int aLen = a.length;
        int bLen = b.length;

        //    @SuppressWarnings("unchecked")
        T[] c = (T[]) Array.newInstance(a.getClass().getComponentType(), aLen + bLen);
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);

        return c;
    }
    public static <T> List<? extends Element> concatenate(List<T> a, List<T> b) {

        List<T> c = new ArrayList<>();
        c.addAll(a);
        c.addAll(b);


        return (List<? extends Element>) c;
    }
    String concat_string(String[] str_to_join)
    {
        String result="";
        for(int i=0;i<str_to_join.length;i++)
        {
            result=result+(i==0?"":",")+"\""+str_to_join[i]+"\"";
        }
        return result;

    }
    String concat_string(ArrayList<String> str_to_join)
    {
        String result="";
        for(int i=0;i<str_to_join.size();i++)
        {
            result=result+(i==0?"":",")+"\""+str_to_join.get(i)+"\"";
        }
        return result;

    }
    String concat_string_p(ArrayList<String> str_to_join)
    {
        String result="";
        for(int i=0;i<str_to_join.size();i++)
        {
            result=result+(i==0?"":",")+str_to_join.get(i);
        }
        return result;

    }
 String concat_map(HashMap<String, String>  M,Boolean key)
    {
        String result="";
        int i=0;
        for(String st :key?M.keySet():M.values())
        {
            result=result+(i==0?"":",")+st;
     i++;
        }
        return result;

    }
    String[] split_string(String str_to_split,String  character)
    {
        if(str_to_split.length()<1){return new String[0];}
        if(!str_to_split.contains(character)) { return new String[]{str_to_split}; }
       return str_to_split.split(character);



    }
    private void writeSourceFile(TypeElement originatingType) {
        //get Log class from android.util package
        //This will make sure the Log class is properly imported into our class
        ClassName logClassName = ClassName.get("android.util", "Log");

        //get the current annotated class name
        TypeVariableName typeVariableName = TypeVariableName.get(originatingType.getSimpleName().toString());

        //create static void method named log
        MethodSpec log = MethodSpec.methodBuilder("LOG")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(void.class)
                //Parameter variable based on the annotated class
                .addParameter(typeVariableName, "DATA")
                //add a Lod.d("ClassName", String.format(class fields));
                .addStatement("$T.d($S, $S)", logClassName, originatingType.getSimpleName().toString(),originatingType.getSimpleName())
                .build();

        //create a class to wrap our method
        //the class name will be the annotated class name + _Log
        TypeSpec loggerClass = TypeSpec.classBuilder(originatingType.getSimpleName().toString() + "_LOG")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                //add the log statetemnt from above
                .addMethod(log)
                .build();
        //create the file
        JavaFile javaFile = JavaFile.builder(originatingType.getEnclosingElement().toString(), loggerClass)
                .build();

        try {
            javaFile.writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
            Messager messager = processingEnv.getMessager();
            String message = String.format("Unable to write file: %s",
                    e.getMessage());
            messager.printMessage(Diagnostic.Kind.WARNING, message);
        }
    }
}