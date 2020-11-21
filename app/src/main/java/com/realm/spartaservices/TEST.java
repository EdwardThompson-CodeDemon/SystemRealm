package com.realm.spartaservices;


import com.realm.annotations.DynamicClass;
import com.realm.annotations.DynamicProperty;

@DynamicClass(table_name = "test_table")
public class TEST {

    @DynamicProperty(json_key = "name",column_name = "name_s")
   public String name="";


    @DynamicProperty(json_key = "text_f",column_name = "text_f")
    public String text_f="";
}
