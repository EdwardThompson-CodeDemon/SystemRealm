package com.realm.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
@Repeatable(SyncDescriptions.class)
public @interface SyncDescription {
    String service_id() default "null";
    String service_name() default "Annonymous";
    String[] table_filters() default {};
    String upload_link() default "";
    String download_link() default "";
    service_type service_type() default service_type.Null;
    int chunk_size() default 1000;
    boolean use_download_filter() default true;
    boolean cs_service() default true;
    String download_array_position() default "JO:Result";
    String upload_array_position() default "";
    String is_ok_position() default "JO:IsOkay";



     enum service_type{
        Null,
        Upload,
        Download,
        Download_Upload,
        Configuration
    }

     class ServiceParams {
        String authenticationurl,result_array_name,isok_name;
        boolean check_isok=true;


        public ServiceParams setAuthenticationUrl(String url)
        {
            authenticationurl=url;
            return this;
        }
        public ServiceParams setResultArrayName(String result_array_name)
        {
            this.result_array_name=result_array_name;
            return this;
        }
    }




}
